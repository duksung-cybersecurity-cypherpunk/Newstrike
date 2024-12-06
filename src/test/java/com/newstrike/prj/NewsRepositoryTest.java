package com.newstrike.prj;

import com.newstrike.prj.domain.News;
import com.newstrike.prj.repository.NewsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Test
    @Transactional
    public void testSaveMultipleNews() {
        List<News> newsList = Arrays.asList(
                News.builder()
                        .title("테스트 뉴스 1")
                        .content("테스트 뉴스 1\n" +
                                "테스트 뉴스 1\n" )
                        .createDate(LocalDateTime.now())
                        .likeCount(0)
                        .scrapCount(0)
                        .build(),
                News.builder()
                        .title("테스트 뉴스 2")
                        .content("두 번째 뉴스 내용\n" +
                                "테스트를 위한 내용입니다.")
                        .createDate(LocalDateTime.now())
                        .likeCount(0)
                        .scrapCount(0)
                        .build(),
                News.builder()
                        .title("테스트 뉴스 3")
                        .content("세 번째 뉴스 내용\n" +
                                "테스트를 위한 내용입니다.")
                        .createDate(LocalDateTime.now())
                        .likeCount(0)
                        .scrapCount(0)
                        .build(),
                News.builder()
                        .title("테스트 뉴스 4")
                        .content("네 번째 뉴스 내용\n" +
                                "테스트를 위한 내용입니다.")
                        .createDate(LocalDateTime.now())
                        .likeCount(0)
                        .scrapCount(0)
                        .build(),
                News.builder()
                        .title("테스트 뉴스 5")
                        .content("다섯 번째 뉴스 내용\n" +
                                "테스트를 위한 내용입니다.")
                        .createDate(LocalDateTime.now())
                        .likeCount(0)
                        .scrapCount(0)
                        .build()
        );

        newsRepository.saveAll(newsList);

        List<News> savedNews = newsRepository.findAll();
        assertThat(savedNews.size()).isEqualTo(5);
    }
}
