package com.newstrike.prj.controller;

import com.newstrike.prj.dto.ApiResponse;
import com.newstrike.prj.dto.NewsListRequest;
import com.newstrike.prj.dto.LLMRequest;
import com.newstrike.prj.dto.LikeScrapResponseDto;
import com.newstrike.prj.dto.LikeScrapListRequest;
import com.newstrike.prj.domain.News;
import com.newstrike.prj.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import org.springframework.http.ResponseEntity;  // ResponseEntity 클래스 import
import com.newstrike.prj.dto.NewsResponse;

import org.springframework.web.bind.annotation.RequestHeader; // 추가
import static com.newstrike.prj.BaseResponseStatus.INVALID_NEWS_ID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/news")
@CrossOrigin(origins = "*")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private NewsService newsService;

    // News 엔티티를 NewsListRequest DTO로 변환하는 헬퍼 메서드
    private NewsListRequest convertToDTO(News news) {
        return new NewsListRequest(
            news.getId(),
            news.getTitle(),
            news.getPublishDate(),
            news.getLikeCount(),
            news.getScrapCount(),
            news.getThumbnail()
        );
    }

    // 뉴스 목록을 가져오는 엔드포인트
    @GetMapping
    public ApiResponse<NewsListRequest> getNews(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "latest") String sortType) { // 정렬 기준 추가
        
        // 로깅 추가: 요청된 페이지, 사이즈, 검색어 및 정렬 기준 확인
        logger.info("Request received for page: {}, size: {}, searchTerm: {}, sortType: {}", page, size, searchTerm, sortType);

        Page<NewsListRequest> newsPage;

        // sortType에 따라 최신순 또는 인기순 정렬
        if (sortType.equals("popular")) {
            newsPage = newsService.getNewsByPageAndSearchTermAndLikes(page, size, searchTerm, true); // 인기순
        } else {
            newsPage = newsService.getNewsByPageAndSearchTerm(page, size, searchTerm); // 최신순 (기본값)
        }

        // 로깅 추가: 반환될 현재 페이지 정보, 전체 페이지 수, 전체 아이템 수 확인
        logger.info("Returning current page: {}, total pages: {}, total items: {}", 
                    newsPage.getNumber() + 1, 
                    newsPage.getTotalPages(), 
                    newsPage.getTotalElements());

        return new ApiResponse<>(
                newsPage.getContent(), // 실제 뉴스 리스트
                newsPage.getNumber() + 1, // 현재 페이지 (0-indexed이므로 +1)
                newsPage.getTotalPages(), // 전체 페이지 수
                newsPage.getTotalElements() // 전체 항목 수
        );
    }


    // 좋아요 토글
    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<LikeScrapResponseDto>> toggleLike(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        LikeScrapResponseDto response = newsService.toggleLikeStatus(id, token);
        return ResponseEntity.ok(new ApiResponse<>(true, 1000, "좋아요 상태가 변경되었습니다.", Collections.singletonList(response)));
    }

    // 스크랩 토글
    @PostMapping("/{id}/scrap")
    public ResponseEntity<ApiResponse<LikeScrapResponseDto>> toggleScrap(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        LikeScrapResponseDto response = newsService.toggleScrapStatus(id, token);
        return ResponseEntity.ok(new ApiResponse<>(true, 1000, "스크랩 상태가 변경되었습니다.", Collections.singletonList(response)));
    }

    @GetMapping("/scrapped")
    public LikeScrapListRequest<NewsListRequest> getScrappedNews(@RequestHeader("Authorization") String token) {
        // News 엔티티 리스트를 가져옴
        List<News> scrappedNews = newsService.getScrappedNewsByUser(token);

        // List<News>를 List<NewsListRequest>로 변환
        List<NewsListRequest> newsList = scrappedNews.stream()
            .map(this::convertToDTO)  // 각 News 엔티티를 DTO로 변환
            .collect(Collectors.toList());

        return new LikeScrapListRequest<>(newsList);
    }


    @GetMapping("/liked")
    public LikeScrapListRequest<NewsListRequest> getLikedNews(@RequestHeader("Authorization") String token) {
        // News 엔티티 리스트를 가져옴
        List<News> likedNews = newsService.getLikedNewsByUser(token);

        // List<News>를 List<NewsListRequest>로 변환
        List<NewsListRequest> newsList = likedNews.stream()
            .map(this::convertToDTO)  // 각 News 엔티티를 DTO로 변환
            .collect(Collectors.toList());

        return new LikeScrapListRequest<>(newsList);
    }



    // 특정 뉴스 ID를 기반으로 뉴스 상세 정보를 가져오는 엔드포인트
    // 뉴스 상세 페이지: 좋아요 및 스크랩 여부를 JWT 토큰을 통해 확인
    @GetMapping("/{id}")
    public ApiResponse<NewsResponse> getNewsById(@PathVariable Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            NewsResponse newsResponse = newsService.getNewsWithUserStatus(id, token); // 수정된 서비스 호출
            if (!newsResponse.getNews().isPublished()) {
                return new ApiResponse<>(false, 4001, "잘못된 접근입니다.", null);
            }
            return new ApiResponse<>(Collections.singletonList(newsResponse), 1, 1, 1L);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals(INVALID_NEWS_ID.getMessage())) {
                return new ApiResponse<>(false, INVALID_NEWS_ID.getCode(), INVALID_NEWS_ID.getMessage(), null);
            } else {
                return new ApiResponse<>(false, 5000, "Unknown error", null);
            }
        }
    }

    @GetMapping("/{id}/next")
    public ApiResponse<News> getNextPublishedNews(@PathVariable Long id, 
                                                @RequestParam(defaultValue = "latest") String sortType) {
        try {
            // 정렬된 리스트에서 다음 뉴스를 가져옴
            News nextNews = newsService.getNextPublishedNews(id, sortType);
            return new ApiResponse<>(Collections.singletonList(nextNews), 1, 1, 1L);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(false, 4001, "다음 뉴스가 없습니다.", null);
        }
    }

    @GetMapping("/{id}/previous")
    public ApiResponse<News> getPreviousPublishedNews(@PathVariable Long id, 
                                                    @RequestParam(defaultValue = "latest") String sortType) {
        try {
            // 정렬된 리스트에서 이전 뉴스를 가져옴
            News previousNews = newsService.getPreviousPublishedNews(id, sortType);
            return new ApiResponse<>(Collections.singletonList(previousNews), 1, 1, 1L);
        } catch (IllegalArgumentException e) {
            return new ApiResponse<>(false, 4002, "이전 뉴스가 없습니다.", null);
        }
    }

    // LLM 데이터를 기반으로 뉴스를 생성하는 엔드포인트 추가
    @PostMapping("/llm")
    public ApiResponse<News> createNewsFromLLM(
            @RequestBody LLMRequest llmRequest) {
        
        News news = newsService.createNewsFromLLM(
                llmRequest.getTitle(),
                llmRequest.getContent(),
                llmRequest.getSource(),
                llmRequest.getLink(),
                llmRequest.getFiveWOneH()
        );
        return new ApiResponse<>(Collections.singletonList(news), 1, 1, 1L);
    }

} 
    
