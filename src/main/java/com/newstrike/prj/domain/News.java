package com.newstrike.prj.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,  columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false)
    private String thumbnail;

    @Column(nullable = false,  columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createDate;  // LocalDate -> LocalDateTime 변경

    @Column
    private LocalDateTime publishDate;  // LocalDate -> LocalDateTime 변경

    @Column(columnDefinition = "TEXT")
    private String source;

    @Column(columnDefinition = "TEXT")
    private String link;

    @Column(columnDefinition = "TEXT")
    private String fiveWOneH;

    @Builder.Default
    @Column
    private Integer likeCount = 0;

    @Builder.Default
    @Column
    private Integer scrapCount = 0;

    @Builder.Default
    @Column(name = "is_published")
    private boolean isPublished = false;

    @OneToMany(mappedBy = "news", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Scrap> scraps = new ArrayList<>();

    @OneToMany(mappedBy = "news", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    private List<Like> likes = new ArrayList<>();

    // 좋아요 수 증가 메서드
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // 좋아요 수 감소 메서드
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // 스크랩 수 증가 메서드
    public void incrementScrapCount() {
        this.scrapCount++;
    }

    // 스크랩 수 감소 메서드
    public void decrementScrapCount() {
        if (this.scrapCount > 0) {
            this.scrapCount--;
        }
    }
}