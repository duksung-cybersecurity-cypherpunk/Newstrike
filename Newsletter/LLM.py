import sys
import requests
from crawl_clustering import crawl, clustering
from fiveW1H_generative_GPT import main as fiveW1H_generative
from inference import main as newsletter_generative
import torch

try:
    # 크롤링 및 클러스터링 함수 호출
    contents_list, links_list = crawl()
    
    # 클러스터링 함수 실행
    news_account = clustering(contents_list, links_list)

    # 육하원칙 생성 함수 호출
    news_account = fiveW1H_generative(news_account)

    # 뉴스레터 생성 함수 호출
    news_account = newsletter_generative(news_account)

    # Spring Boot API에 전송할 데이터 준비 및 전송
    for label in range(len(news_account)):
        data = {
            "source": news_account[label][0],  # 원본 내용 (source)
            "link": news_account[label][1],  # 링크 (link)
            "fiveWOneH": news_account[label][2],  # 육하원칙 (5W1H)
            "title": news_account[label][3],  # 뉴스 제목
            "content": news_account[label][4]  # 뉴스 내용
        }

        # API 호출
        response = requests.post("https://humble-commonly-goshawk.ngrok-free.app/api/v1/news/llm", json=data)
        
        if response.status_code == 200:
            print(f"Label {label}의 뉴스가 성공적으로 저장되었습니다.")
        else:
            print(f"Label {label}의 뉴스를 저장하는데 실패했습니다. 상태 코드: {response.status_code}")
            print(f"오류 메시지: {response.text}")

finally:
    # GPU 메모리 해제
    print("GPU 메모리 해제 중...")
    torch.cuda.empty_cache()

    # 프로그램 종료
    print("프로그램 종료")
    sys.exit(0)
