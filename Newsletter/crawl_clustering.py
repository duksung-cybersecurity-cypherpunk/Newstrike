from selenium import webdriver
from selenium.webdriver.common.by import By
import datetime
import hdbscan
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_distances


# 크롤링 함수
def crawl():
    options = webdriver.ChromeOptions()
    options.add_argument('--disable-gpu')
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-blink-features=AutomationControlled')
    options.add_argument('--disable-dev-shm-usage')
    options.add_argument('--disable-software-rasterizer')

    driver = webdriver.Chrome(options=options)

    # 어제 날짜 설정정
    current_date = datetime.date.today() - datetime.timedelta(days=1)
  
    start_date = current_date
    end_date = current_date
    
    # 크롤링한 기사의 content와 link를 저장할 리스트 생성
    contents_list = []
    links_list = []
    
    # 스포츠타임즈
    page = 1
    while True:
        driver.get(f"http://www.thesportstimes.co.kr/news/articleList.html?page={page}&total=7722&sc_section_code=S1N1&sc_sub_section_code=&sc_serial_code=&sc_area=&sc_level=&sc_article_type=&sc_view_level=&sc_sdate=&sc_edate=&sc_serial_number=&sc_word=&sc_word2=&sc_andor=&sc_order_by=E&view_type=sm")

        titles = driver.find_elements(By.CLASS_NAME, "list-titles")

        href_values = []
        links = driver.find_elements(By.XPATH, "//td[@class='list-titles']//a")

        for link in links:
            href_value = link.get_attribute('href')
            href_values.append(href_value)

        for href in href_values:
            driver.get(href)

            dates = driver.find_elements(By.XPATH, "//span[@class='info-txt']")
            if len(dates) > 0:
                date = (dates[0].text).split()[-2]
                date_object = datetime.datetime.strptime(date, "%Y.%m.%d")
                date = date_object.date()
            else:
                date = None
    
            # 날짜 정보가 없는 기사는 date 객체로 변환을 못 시켜서 제외
            if date is not None:
                if (start_date <= date):
                    if(start_date <= date <= end_date):
                        contents = driver.find_elements(By.XPATH, "//div[@class='cont-body']//p")
                        content_text = '\n'.join([content.text for content in contents])
                        contents_list.append(content_text)
                        links_list.append(href)
                else:
                    break
            else:
                pass
        if date != None:
            if date < start_date:
                break
        page += 1
        
    #노컷뉴스
    nocut_date = start_date.strftime("%Y-%m-%d")
    driver.get(f"https://www.nocutnews.co.kr/news/sports/list?d={nocut_date}&c2=220")
    titles = driver.find_elements(By.XPATH, "//ul[@class='newslist']//li//dl//dt//a//strong")
    href_values = []
    links = driver.find_elements(By.XPATH, "//ul[@class='newslist_b']//li//a")
    for link in links:
        href_value = link.get_attribute('href')
        href_values.append(href_value)
    for href in href_values:
        driver.get(href)
        dates = driver.find_elements(By.XPATH, "//ul[@class='bl_b']/li[2]")
        if len(dates) > 0:
            date = (dates[0].text).split()[-2]
            date_object = datetime.datetime.strptime(date, "%Y-%m-%d")
            date = date_object.date()
        else:
            dates = driver.find_elements(By.XPATH, "//ul[@class='bl_b']/li")
            date = (dates[0].text).split()[-2] 
            date_object = datetime.datetime.strptime(date,"%Y-%m-%d")
            date = date_object.date()
        
        if (start_date <= date):
            if(start_date <= date <= end_date):
                contents = driver.find_elements(By.XPATH, "//div[@id='pnlContent']")
                content_text = '\n'.join([content.text for content in contents])
                contents_list.append(content_text)
                links_list.append(href)
        else:
            break
    
    #스포츠월드
    print("=============================스포츠월드 내용 가져오기 =============================")
    page = 0
    while True:
        world_start_date = start_date.strftime("%Y-%m-%d")
        world_end_date = end_date.strftime("%Y-%m-%d")
        print(world_start_date)
        print(world_end_date)
        driver.get(f"https://www.sportsworldi.com/list/1105050000000?page={page}")

        titles = driver.find_elements(By.XPATH, "//strong[@class='tit']")

        href_values = []
        links = driver.find_elements(By.XPATH, "//ul[@class='listBox']//li//a")

        for link in links:
            href_value = link.get_attribute('href')
            href_values.append(href_value)

        for href in href_values:
            driver.get(href)

            dates = driver.find_elements(By.XPATH, "//p[@class='viewInfo']")
            if len(dates) > 0:
                date = dates[0].text
                if "입력" in date and "수정" in date:
                    last_modified = date.split("수정")[-1].strip()

                    date = (last_modified.split()[-2])
                elif "입력" in date:
                    date = date.split()[-2] 
            else:
                date = None
            
            # 날짜 정보가 없는 기사는 date 객체로 변환을 못 시켜서 제외
            if date is not None:
                if(world_start_date <= date):
                    if(world_start_date <= date <= world_end_date):
                        titles = driver.find_elements(By.XPATH, "//h3[@id='title_sns']")
                        for title in titles:
                            print(title.text)
                        print(date)

                        contents = driver.find_elements(By.XPATH, "//article[@class='viewBox2']")
                        content_text = '\n'.join([content.text for content in contents])
                        contents_list.append(content_text)
                        links_list.append(href)
                        print(href)

                        print("\n")

                else:
                    break 
            else:
                print("날짜 정보가 없어 기사를 스킵합니다.")
                titles = driver.find_elements(By.XPATH, "//div[@class='article_top']//h1[@class='article-title']")
                for title in titles:
                            print("제목: ", title.text)
                print(href)
        
        if date != None:
            if date < world_start_date:
                break
        page += 1
        
    #스포츠조선
    page = 1
    while True:
        driver.get(f"https://sports.chosun.com/baseball/?action=baseball&page={page}")

        href_values = []
        links = driver.find_elements(By.XPATH, "//div[@class='sub-list-post']//div[@class='post-data']//a")

        for link in links:
            href_value = link.get_attribute('href')
            href_values.append(href_value)


        for href in href_values:
            driver.get(href)

            dates = driver.find_elements(By.XPATH, "//span[@class='article-day']")
            if len(dates) > 1:
                date = dates[1].text
                if "기사입력" in date and "최종수정" in date:
                    last_modified = date.split("|")[-1].strip()
                    date = (last_modified.split()[-2])
                    date_object = datetime.datetime.strptime(date, "%Y-%m-%d")
                    date = date_object.date()
                elif "기사입력" in date:
                    date = date.split()[-2] 
                    date_object = datetime.datetime.strptime(date, "%Y-%m-%d")
                    date = date_object.date()
                else:
                    date = None
            elif len(dates) == 1:
                date = dates[0].text
                if "기사입력" in date and "최종수정" in date:
                    last_modified = date.split("|")[-1].strip()
                    date = (last_modified.split()[-2])
                    date_object = datetime.datetime.strptime(date, "%Y-%m-%d")
                    date = date_object.date()
                elif "기사입력" in date:
                    date = date.split()[-2] 
                    date_object = datetime.datetime.strptime(date, "%Y-%m-%d")
                    date = date_object.date()
                else:
                    date = None

            else:
                date = None
            # 날짜 정보가 없는 기사는 date 객체로 변환을 못 시켜서 제외
            if date is not None:
                if (start_date <= date):
                    if(start_date <= date <= end_date):
                
                        contents = driver.find_elements(By.XPATH, "//div[@class='news_text']")
                        content_text = '\n'.join([content.text for content in contents])
                        contents_list.append(content_text)
                        links_list.append(href)
                else:
                    break
            else:
                pass
        if date != None:
            if date < start_date:
                break
        page += 1
    driver.quit()
    return contents_list, links_list
  
  
# 클러스터링 함수
def clustering(contents_list, links_list):
    model = SentenceTransformer('jhgan/ko-sroberta-multitask')
    embeddings = model.encode(contents_list)
    clusterer = hdbscan.HDBSCAN(min_cluster_size=2)
    cluster_labels = clusterer.fit_predict(embeddings)
    
    # 대표 기사 선정 함수 호출
    central_articles_indices = select_central_articles(embeddings, cluster_labels)
    
    # 리스트에 각 레이블별 대표기사 centent, link 저장
    news_account = [[0 for _ in range(5)] for _ in range(len(central_articles_indices))]
    
    for label, idx in central_articles_indices.items():
        news_account[label][0] = contents_list[idx]
        news_account[label][1] = links_list[idx]
    return news_account
  
  
# 대표 기사 선정  함수
def select_central_articles(embeddings, cluster_labels):
    central_articles_indices = {}
    for label in set(cluster_labels):
        if label == -1:
            continue
        indices = [i for i, lbl in enumerate(cluster_labels) if lbl == label]
        if len(indices) > 1:
            cluster_embeddings = embeddings[indices]
            distances = cosine_distances(cluster_embeddings)
            average_distances = distances.mean(axis=0)
            central_idx = indices[average_distances.argmin()]
            central_articles_indices[label] = central_idx
        else:
            print(f"Cluster {label} has insufficient articles for analysis.")
    return central_articles_indices
  
  
def main():
    contents_list, links_list = crawl()
    news_account = clustering(contents_list, links_list)
    
if __name__ == '__main__':
    main()
    