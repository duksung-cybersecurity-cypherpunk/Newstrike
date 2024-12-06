package com.newstrike.prj.domain;

import jakarta.persistence.*;  // JPA 관련 어노테이션을 import
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "scrap")
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    // 스크랩한 날짜 추가
    private LocalDateTime scrappedDate;

    // 생성자 추가: id 없이 user, news와 scrappedDate 받는 생성자
    public Scrap(Users user, News news) {
        this.user = user;
        this.news = news;
        this.scrappedDate = LocalDateTime.now();  // 현재 시간을 저장
    }
}
