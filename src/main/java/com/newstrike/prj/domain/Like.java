package com.newstrike.prj.domain;

import jakarta.persistence.*;  // JPA 관련 어노테이션을 import
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_likes")  // 테이블 이름 변경
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 좋아요한 날짜 추가
    private LocalDateTime likedDate;

    // 생성자 추가: id 없이 user, news와 likedDate 받는 생성자
    public Like(Users user, News news) {
        this.user = user;
        this.news = news;
        this.likedDate = LocalDateTime.now();  // 현재 시간을 저장
    }
}
