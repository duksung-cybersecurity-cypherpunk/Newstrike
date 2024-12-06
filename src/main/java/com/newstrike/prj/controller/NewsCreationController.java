package com.newstrike.prj.controller;

import com.newstrike.prj.dto.ApiResponse;
import com.newstrike.prj.domain.News;
import com.newstrike.prj.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1/news")
public class NewsCreationController {

    @Autowired
    private NewsService newsService;

    // 뉴스 생성 엔드포인트
    @PostMapping
    public ApiResponse<News> createNews(@RequestParam String title, @RequestParam String content) {
        // try {
            News news = newsService.createNews(title, content);
            return new ApiResponse<>(Collections.singletonList(news), 1, 1, 1L);
        // } catch (IllegalArgumentException e) {
        //     if (e.getMessage().equals(INVALID_NEWS_TITLE.getMessage())) {
        //         return new ApiResponse<>(false, INVALID_NEWS_TITLE.getCode(), INVALID_NEWS_TITLE.getMessage());
        //     } else if (e.getMessage().equals(INVALID_NEWS_CONTENT.getMessage())) {
        //         return new ApiResponse<>(false, INVALID_NEWS_CONTENT.getCode(), INVALID_NEWS_CONTENT.getMessage());
        //     } else {
        //         return new ApiResponse<>(false, 5000, "Unknown error");
        //     }
        }
    }

