from fastapi import FastAPI
from pydantic import BaseModel
import openai
from whoosh.index import open_dir
from whoosh.qparser import QueryParser, MultifieldParser, OrGroup
import faiss
from langchain.embeddings import OpenAIEmbeddings
from transformers import AutoModelForSequenceClassification, AutoTokenizer, AutoModelForCausalLM
import torch
import os
import json
import pickle
import numpy as np
from konlpy.tag import Okt


# 임베딩 및 벡터 스토어 로드
embd = OpenAIEmbeddings(model="text-embedding-ada-002")

# FAISS 인덱스와 메타데이터 불러오기
def load_faiss_index(index_path="faiss_index1107.bin", metadata_path="metadata_store1107.pkl"):
    index = faiss.read_index(index_path)
    with open(metadata_path, "rb") as f:
        faiss_ids, metadata_store = pickle.load(f)
    return index, faiss_ids, metadata_store

index, faiss_ids, metadata_store = load_faiss_index()

# Whoosh 인덱스 로드
index_dir = "indexdir1107"
ix = open_dir(index_dir)

# LLM 모델과 토크나이저 설정
model_id = "Bllossom/llama-3-Korean-Bllossom-70B"
tokenizer = AutoTokenizer.from_pretrained(model_id)
model = AutoModelForCausalLM.from_pretrained(model_id, torch_dtype="auto", device_map="auto")
tokenizer.pad_token = tokenizer.eos_token

# FastAPI 인스턴스 생성
app = FastAPI()

# 요청 모델 정의
class ChatRequest(BaseModel):
    prompt: str

# FAISS 기반 유사도 검색 함수
def semantic_search(prompt: str, top_k: int = 3):
    query_embedding = np.array([embd.embed_query(prompt)], dtype="float32")
    _, faiss_indices = index.search(query_embedding, top_k)

    semantic_results = []
    for idx in faiss_indices[0]:
        if idx < len(faiss_ids):  # 유효한 인덱스인지 확인
            unique_id = faiss_ids[idx]
            metadata = metadata_store[unique_id]
            semantic_results.append({
                "content": metadata["content"],
                "source": metadata["source"],
                "page_or_row_num": metadata["page_or_row_num"],
                "chunk_num": metadata["chunk_num"]
            })
    return semantic_results

# Okt 형태소 분석기 초기화
okt = Okt()

# BM25 키워드 검색 함수
def bm25_search(prompt: str, top_k: int = 2):
    # 불용어 리스트 정의 (조사와 불필요한 단어)
    stopwords = {"의", "를", "에", "에서", "이", "가", "을", "와", "과", "은", "는", "도", "으로", "한테", "에게", "로"}

    # 형태소 분석을 통해 단어와 품사를 추출하고, 조사 제외한 주요 단어만 남기기
    tokens = okt.pos(prompt)  # 형태소와 품사 추출
    keywords = [word for word, pos in tokens if pos != 'Josa' and word not in stopwords]

    # 핵심 단어로 새로운 검색 쿼리 생성
    modified_prompt = " ".join(keywords)

    keyword_results = []
    with ix.searcher() as searcher:
        # OR 연산자로 모든 단어 포함 검색
        query_parser = MultifieldParser(["content"], ix.schema, group=OrGroup)
        whoosh_query = query_parser.parse(modified_prompt)
        print(f"Whoosh query: {whoosh_query}")

        # 검색 수행
        whoosh_results = searcher.search(whoosh_query, limit=top_k)
        for hit in whoosh_results:
            keyword_results.append({
                "content": hit["content"],
                "source": hit["source"],
                "page_or_row_num": hit["page_or_row_num"],
                "chunk_num": hit["chunk_num"]
            })
    return keyword_results

# 리랭킹 함수
def rerank_results(query, results, model, tokenizer):
    reranked_scores = []
    with torch.no_grad():
        for result in results:
            inputs = tokenizer(query, result, return_tensors='pt', truncation=True, padding=True, max_length=512)
            outputs = model(**inputs)
            score = outputs.logits[0].item()
            reranked_scores.append(score)
    return reranked_scores

# 앙상블 및 리랭크 함수
def ensemble_and_rerank(prompt, bm25_results, semantic_results):
    # BM25와 벡터 검색 결과를 결합
    combined_results = bm25_results + semantic_results

    # 중복 제거
    final_results = list({json.dumps(result, sort_keys=True, ensure_ascii=False) for result in combined_results})
    print(final_results)

    reranker_model = AutoModelForSequenceClassification.from_pretrained("Dongjin-kr/ko-reranker")
    reranker_tokenizer = AutoTokenizer.from_pretrained("Dongjin-kr/ko-reranker")
    reranker_model.eval()

    reranked_scores = rerank_results(prompt, final_results, reranker_model, reranker_tokenizer)
    # 리랭크 점수를 기준으로 결과 재정렬
    reranked_results = [x for _, x in sorted(zip(reranked_scores, final_results), reverse=True)]

    return reranked_results

def generate_response(prompt: str):
    bm25_results = bm25_search(prompt)
    print("BM25 Search Results:", bm25_results)

    semantic_results = semantic_search(prompt)
    print("Semantic Search Results:", semantic_results) 

    final_results = ensemble_and_rerank(prompt, bm25_results, semantic_results)

    messages = [
        {"role": "user", "content": f"Given Context: {final_results} Give the best full answer about question. {prompt} All answers should be written in Korean And concise."}
    ]

    query = tokenizer.apply_chat_template(messages)

    input_ids = tokenizer.apply_chat_template(
        messages,
        add_generation_prompt=True,
        return_tensors="pt"
    ).to(model.device)

    terminators = [
        tokenizer.eos_token_id,
        tokenizer.convert_tokens_to_ids("<|eot_id|>")
    ]

    outputs = model.generate(
        input_ids,
        max_new_tokens=512,
        eos_token_id=terminators,
        pad_token_id=tokenizer.eos_token_id,
        do_sample=True,
        temperature=1,
        top_p=0.9,
    )
    response = outputs[0][input_ids.shape[-1]:]
    result = tokenizer.decode(response, skip_special_tokens=True)
    return result

# 채팅 엔드포인트 정의
@app.post("/chat")
async def chat(request: ChatRequest):
    response = generate_response(request.prompt)
    return {"response": response}