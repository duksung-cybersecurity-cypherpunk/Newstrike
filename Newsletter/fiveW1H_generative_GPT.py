from openai import OpenAI

# 육하원칙 생성 함수 (gpt 사용)
def get_llm_response(prompt, news):
  client = OpenAI(api_key="")
  response = client.chat.completions.create(
    model="gpt-4o",
    messages=[
      {"role": "system", "content": prompt},
      {"role": "user", "content": news}
    ]
  )
  return response.choices[0].message.content

# 생성된 육하원칙 news_accounts에 저장하는 함수
def process_news_accounts(prompt, news_account):
  for label in range(len(news_account)):
    news = news_account[label][0]
    llm_response = get_llm_response(prompt, news)
    news_account[label][2] = llm_response
  return news_account


def main(news_account):
  prompt = '''당신에게 줄 데이터는 야구 뉴스 기사이다.
  뉴스 기사를 보고 육하원칙에 맞춰서 출력해라.

  1. 경기에 관한 내용이 있으면 "어떻게(how)"에 경기 과정을 자세히 써라.
  2. 내용이 많아지면 1, 2, 3 형식으로 써라
  3. 선수의 이름이나, 숫자, 몇 회말인지 등은 정확히 나타내라.
  4. 감독과 선수의 인터뷰 내용은 육하원칙 마지막에 감독과 선수의 인터뷰 목록을 따로 만들어라.
  5. 감독과 선수의 인터뷰의 내용은 그대로 출력해라.
  6. 감독의 인터뷰, 선수의 인터뷰가 모두 없으면 인터뷰 없음이라고 출력해라. 만약 인터뷰 내용이 하나라도 있으면 인터뷰 없음을 출력하지 마라. 

  예시:
  누가 (Who): 키움 히어로즈와 한화 이글스
  언제 (When): 2024년 5월 7일
  어디서 (Where): 서울 고척스카이돔
  무엇을 (What):
  1. 키움 히어로즈가 한화 이글스와의 2024 신한 SOL 뱅크 KBO리그 홈경기에서 연장 11회에 4-3으로 승리.
  2. 키움 주장 김혜성이 연장 11회말에 끝내기 홈런을 치며 승부에 결정적인 역할을 함.
  3. 경기는 주로 투수전 양상으로 흘러갔으며, 한화의 김민우와 키움의 김선기가 각각 솔로 홈런을 허용.
  4. 키움은 7회말 송성문의 투런 홈런으로 경기를 원점으로 돌려 연장전으로 이끔.
  어떻게 (How):
  1. 한화의 이도윤이 1사 후 이형종의 땅볼 때 실책성 송구를 범해 타자 주자를 살려 보내며 키움에게 동점 기회를 제공.
  2. 한화 투수들이 중요한 순간에 집중력을 잃고 추가 점수를 허용.
  3. 연장 11회 김혜성의 끝내기 홈런으로 경기 종료.
  4. 양 팀은 경기 내내 호수비를 보여주며 체력전 속에서도 집중력을 유지하려 노력, 특히 키움 중견수 이주형이 10회초 중요한 순간에 호수비를 펼침.
  왜 (Why):
  1. 키움은 개막 후 4연패에서 회복하여 7연승을 달리며 리그 상위권으로 도약하고자 함.
  2. 한화는 초기 높은 승률 후 3연패로 분위기가 가라앉으며 승리의 필요성이 증가, 이날 경기에서 15개의 잔루를 남기며 득점 찬스를 살리지 못함.
  감독과 선수의 인터뷰
  1. 김태형 감독: "일단 지금 몸 상태는 이상이 없다"'''

  new_news_account = process_news_accounts(prompt, news_account)
  
  return new_news_account


if __name__ == '__main__':
  pass