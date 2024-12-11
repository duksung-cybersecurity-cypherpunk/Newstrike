import torch
from transformers import AutoModelForCausalLM, AutoTokenizer
import os

# 기사 반복 안 하고 한 번만 나오게 하는 함수
def remove_before_first_star_and_after_fifth_star(text):
  # 첫 번째와 다섯 번째 *의 위치를 저장할 변수
  first_star_index = -1
  fifth_star_index = -1
  star_count = 0

  # 첫 번째와 다섯 번째 *의 위치 찾기
  for index, char in enumerate(text):
    if char == "*":
      star_count += 1
      if star_count == 1:
        first_star_index = index
      elif star_count == 5:
        fifth_star_index = index
        break

  # 첫 번째 *가 없는 경우 원래 문자열 반환
  if first_star_index == -1:
    return text

  # 다섯 번째 *가 없는 경우 첫 번째 * 이후의 문자열 반환
  if fifth_star_index == -1:
    return text[first_star_index:]

  # 첫 번째 * 이후와 다섯 번째 * 이전의 문자열 반환
  return text[first_star_index:fifth_star_index]

# 제목과 내용을 분리해주는 함수
def split_title_and_content(text):
  first_start_index = -1
  fourth_start_index = -1
  star_count = 0

  for index, char in enumerate(text):
    if char == "*":
      star_count += 1
      if star_count == 1:
        first_start_index = index 
      elif star_count == 4:
        fourth_start_index = index 
        break
  
  title = text[first_start_index:fourth_start_index+1]
  content = text[fourth_start_index+1:]
  return title, content

def generative_newsletter(news_account):
  os.environ["huggingface_token"] = ""
  model_path = 'sieun1002/newsletter_5W1H_interview'
  model = AutoModelForCausalLM.from_pretrained(model_path, device_map="auto", use_auth_token=os.environ["huggingface_token"])
  tokenizer = AutoTokenizer.from_pretrained(model_path, use_auth_token=os.environ["huggingface_token"])
  tokenizer.pad_token = tokenizer.eos_token

  instruction = '''You are a newsletter writer who creates newsletters.
  You must generate answers according to the following rules.
  1. Make a new sentence instead of using the sentence in the news provided.
  2. Write the sentences based on the news content provided.
  3. The answer should be no more than 700 characters.
  Remember to write the sentences you make based on the news content provided.
  You must only generate new information based on the contents of the provided news, and not invent anything arbitrarily.
  Never make up new content.
  '''

  for label in range(len(news_account)):
        fiveW1H = news_account[label][2]

        prompt_template = f'''
        instruction: {instruction}  뉴스기사 데이터: {fiveW1H}
        '''
        inputs = tokenizer(prompt_template, return_tensors="pt").to("cuda")

        # Generate
        generate_ids = model.generate(inputs.input_ids, max_length=4096, repetition_penalty = 1)
        generated_text=tokenizer.batch_decode(generate_ids, skip_special_tokens=True, clean_up_tokenization_spaces=False)[0]

        generated_text = remove_before_first_star_and_after_fifth_star(generated_text)
        newsletter_title, newsletter_content = split_title_and_content(generated_text)
        news_account[label][3] = newsletter_title 
        news_account[label][4] = newsletter_content

        # GPU 메모리 캐시 비우기
        torch.cuda.empty_cache()
  return news_account

def main(news_account):
  generative_newsletter(news_account)


  return news_account
  

if __name__ == '__main__':
  pass
