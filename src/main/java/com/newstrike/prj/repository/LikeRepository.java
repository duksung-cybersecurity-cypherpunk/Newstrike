package com.newstrike.prj.repository;

import com.newstrike.prj.domain.News; 
import com.newstrike.prj.domain.Users;
import com.newstrike.prj.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional; 
import java.util.List; 



@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByUser(Users user);
    Optional<Like> findByUserAndNews(Users user, News news);
        // existsByUserAndNews 메서드 추가
    boolean existsByUserAndNews(Users user, News news);
}

