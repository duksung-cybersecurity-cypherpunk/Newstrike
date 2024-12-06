package com.newstrike.prj.controller;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.newstrike.prj.domain.News;
import com.newstrike.prj.dto.AdminNewsRequest;
import com.newstrike.prj.dto.ApiResponse;
import com.newstrike.prj.dto.NewsUpdateRequest;
import com.newstrike.prj.repository.NewsRepository;
import com.newstrike.prj.service.AdminService;
import com.newstrike.prj.service.NewsService;

import static com.newstrike.prj.BaseResponseStatus.INVALID_NEWS_TITLE;
import static com.newstrike.prj.BaseResponseStatus.INVALID_NEWS_CONTENT;
import static com.newstrike.prj.BaseResponseStatus.INVALID_NEWS_ID;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsRepository newsRepository;

    // 뉴스 목록을 가져오는 엔드포인트
    @GetMapping
    public ApiResponse<AdminNewsRequest> getNews(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10")int size) {
        
        // 로깅 추가: 요청된 페이지와 사이즈 확인
        logger.info("Request received for page: {}, size: {}", page, size);

        Page<AdminNewsRequest> newsPage = adminService.getNewsByPage(page, size, searchTerm);
        
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

     // 특정 뉴스 ID를 기반으로 뉴스 상세 정보를 가져오는 엔드포인트
    @GetMapping("/{id}")
    public ApiResponse<News> getNewsById(@PathVariable Long id) {
        try {
            News news = newsService.getNewsById(id);
            return new ApiResponse<>(Collections.singletonList(news), 1, 1, 1L);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals(INVALID_NEWS_ID.getMessage())) {
                return new ApiResponse<>(false, INVALID_NEWS_ID.getCode(), INVALID_NEWS_ID.getMessage());
            } else {
                return new ApiResponse<>(false, 5000, "Unknown error", null);
            }
        }
    }

    // 뉴스 생성 엔드포인트
    @PutMapping("/update")
    public ApiResponse<News> updateNews(@RequestBody NewsUpdateRequest request) {
        try {
            // ID로 뉴스 검색
            News existingNews = newsService.getNewsById(request.getId());
            
            // 제목과 내용 업데이트
            existingNews.setTitle(request.getTitle());
            existingNews.setContent(request.getContent());
            
            // 수정된 뉴스 저장
            News updatedNews = newsRepository.save(existingNews);
            
            return new ApiResponse<>(Collections.singletonList(updatedNews),  // 뉴스 리스트로 생성
                1,  // 단일 아이템이므로 현재 페이지는 1
                1,  // 단일 아이템이므로 전체 페이지도 1
                1L  // 총 1개의 아이템
            );
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals(INVALID_NEWS_TITLE.getMessage())) {
                return new ApiResponse<>(false, INVALID_NEWS_TITLE.getCode(), INVALID_NEWS_TITLE.getMessage());
            } else if (e.getMessage().equals(INVALID_NEWS_CONTENT.getMessage())) {
                return new ApiResponse<>(false, INVALID_NEWS_CONTENT.getCode(), INVALID_NEWS_CONTENT.getMessage());
            } else if (e.getMessage().equals(INVALID_NEWS_ID.getMessage())) {
                return new ApiResponse<>(false, INVALID_NEWS_ID.getCode(), INVALID_NEWS_ID.getMessage());
            } else {
                return new ApiResponse<>(false, 5000, "Unknown error");
            }
        }
    }



    @DeleteMapping("/delete")
    public ApiResponse<Void> deleteNews(@RequestParam List<Long> ids) {
        try {
            // 로깅 추가: 요청된 삭제 뉴스 ID 확인
            logger.info("Request received to delete news with ids: {}", ids);

            // 뉴스 삭제
            newsService.deleteNewsByIds(ids);

            // 로깅 추가: 삭제 완료
            logger.info("Deleted news with ids: {}", ids);
            
            return new ApiResponse<>(true, 2000, "News deleted successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting news: {}", e.getMessage());
            return new ApiResponse<>(false, 5000, "Error deleting news");
        }
    }

    @PutMapping("/publish")
    public ApiResponse<Void> publishNews(@RequestParam List<Long> ids) {
        try {
            // 로깅 추가: 요청된 게시 뉴스 ID 확인
            logger.info("Request received to publish news with ids: {}", ids);

            // 뉴스 게시 상태 업데이트
            newsService.publishNewsByIds(ids);

            // 로깅 추가: 게시 완료
            logger.info("Published news with ids: {}", ids);
            
            return new ApiResponse<>(true, 2000, "News published successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error publishing news: {}", e.getMessage());
            return new ApiResponse<>(false, 5000, "Error publishing news");
        }
    }

    @PutMapping("/unpublish/{id}")
    public ApiResponse<Void> unpublishNews(@PathVariable Long id) {
        try {
            // 로깅 추가: 요청된 뉴스 ID 확인
            logger.info("Request received to unpublish news with id: {}", id);

            // 뉴스 게시 상태를 false로 업데이트
            newsService.unpublishNewsById(id);

            // 로깅 추가: 게시 취소 완료
            logger.info("Unpublished news with id: {}", id);
            
            return new ApiResponse<>(true, 2000, "News unpublished successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Error unpublishing news: {}", e.getMessage());
            return new ApiResponse<>(false, 5000, "Error unpublishing news");
        }
    }
}
