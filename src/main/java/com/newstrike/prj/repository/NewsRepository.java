package com.newstrike.prj.repository;

import com.newstrike.prj.domain.News;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    // isPublished가 true인 뉴스만 가져오는 메서드
    Page<News> findByIsPublishedTrue(Pageable pageable);
    
    // 다음 페이지 불러오기
    Optional<News> findFirstByIdGreaterThanAndIsPublishedTrueOrderByIdAsc(Long currentId);
    
    // 이전 페이지 불러오기
    Optional<News> findFirstByIdLessThanAndIsPublishedTrueOrderByIdDesc(Long currentId);
    
    // 발행된 뉴스 중 검색
    Page<News> findByTitleContainingAndIsPublishedTrueOrContentContainingAndIsPublishedTrue(String title, String content, Pageable pageable);
    
    // 뉴스 중 검색
    Page<News> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    
    // 최신 3개 뉴스 정렬
    List<News> findTop3ByIsPublishedTrueOrderByPublishDateDesc();
    
    // 발행된 뉴스 중 최신 정렬
    List<News> findAllByOrderByPublishDateDesc();
    
    // 발행된 뉴스 중 최근 일주일 내의 뉴스 가져오기 (좋아요 순으로 정렬)
    List<News> findByPublishDateAfterAndIsPublishedTrue(LocalDateTime date, Sort sort);
    
    // 발행된 뉴스 중 일주일 이전의 뉴스 가져오기
    List<News> findByPublishDateBeforeAndIsPublishedTrue(LocalDateTime date, Sort sort);

    // 검색어가 제목 또는 내용에 포함되고, 특정 날짜 이전에 발행된 뉴스 가져오기
    List<News> findByTitleContainingAndIsPublishedTrueOrContentContainingAndIsPublishedTrueAndPublishDateBefore(
            String title, String content, LocalDateTime date, Sort sort);
}