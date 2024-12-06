package com.newstrike.prj.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


import java.time.LocalDate;

import org.hibernate.annotations.GenericGenerator;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwd;

    @Column(nullable = false)
    private String nickname;

    @Builder.Default
    @Column(nullable = false)
    private Integer grade_count = 0;

    @Builder.Default
    @Column(nullable = false)
    private String grade = "아마추어";

    @Builder.Default
    @Column(nullable = false)
    private String next_grade = "퓨쳐스";    

    @Builder.Default
    @Column
    private boolean subscribe = false;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column
    private LocalDate lastLoginDate;

    // 새로운 필드 추가: 다음 등급까지 남은 로그인 횟수
    @Builder.Default
    @Column
    private Integer remainingLogins = 0;
    
    // Many-to-Many 관계를 Like와 Scrap을 통해 연결
    @ManyToMany
    @JoinTable(
        name = "likes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    private List<News> likedNews;

    @ManyToMany
    @JoinTable(
        name = "scraps",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "news_id")
    )
    private List<News> scrappedNews;

}

