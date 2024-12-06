package com.newstrike.prj.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.newstrike.prj.domain.News;
import com.newstrike.prj.dto.AdminNewsRequest;
import com.newstrike.prj.repository.NewsRepository;

@Service
public class AdminService {
    @Autowired
    private NewsRepository newsRepository;

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    // 뉴스 리스트를 페이지네이션하여 반환
    public Page<AdminNewsRequest> getNewsByPage(int page, int size, String searchTerm) {
        // 로깅 추가: 요청된 페이지, 사이즈, 검색어 확인
        logger.info("Fetching page: {}, with size: {}, with search term: {}", page, size, searchTerm);
    
        Page<News> newsPage;
    
        if (searchTerm != null && !searchTerm.isEmpty()) {
            // 검색어가 있는 경우 검색어로 뉴스 조회
            newsPage = newsRepository.findByTitleContainingOrContentContaining(searchTerm, searchTerm, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createDate").and(Sort.by(Sort.Direction.DESC, "id"))));
        } else {
            // 검색어가 없는 경우 전체 뉴스 조회
            newsPage = newsRepository.findAll(PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createDate").and(Sort.by(Sort.Direction.DESC, "id"))));
        }
    
        // 로깅 추가: 전체 아이템 수와 전체 페이지 수 확인
        logger.info("Total elements: {}, Total pages: {}", newsPage.getTotalElements(), newsPage.getTotalPages());
    
        return newsPage.map(news -> new AdminNewsRequest(
                news.getId(),
                news.getTitle(),
                news.getCreateDate(),
                news.getPublishDate()
        ));
    }    
}
