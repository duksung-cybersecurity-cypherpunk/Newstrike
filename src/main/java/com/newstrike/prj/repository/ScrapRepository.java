package com.newstrike.prj.repository;

import com.newstrike.prj.domain.Scrap;
import com.newstrike.prj.domain.News; 
import com.newstrike.prj.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; 
import java.util.List; 

@Repository
public interface ScrapRepository extends JpaRepository<Scrap, Long> {
    List<Scrap> findByUser(Users user);
    Optional<Scrap> findByUserAndNews(Users user, News news);
    // existsByUserAndNews 메서드 추가
    boolean existsByUserAndNews(Users user, News news);
    // 사용자별 스크랩된 뉴스들을 최신순으로 가져오는 메서드
    List<Scrap> findByUserOrderByScrappedDateDesc(Users user);
}
