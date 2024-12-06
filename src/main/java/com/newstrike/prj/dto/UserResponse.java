package com.newstrike.prj.dto;

import com.newstrike.prj.domain.UserRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponse {
    private String email;

    private String nickname;

    private Integer grade_count;

    private String grade;

    private String next_grade;

    private Integer remainingLogins; // 남은 로그인 횟수를 저장하는 필드 추가

    private UserRole role;
    // 생성자 업데이트
    public UserResponse(String email, String nickname, Integer grade_count, String grade, String next_grade, Integer remainingLogins, UserRole role) {
        this.email = email;
        this.nickname = nickname;
        this.grade_count = grade_count;
        this.grade = grade;
        this.next_grade = next_grade;
        this.remainingLogins = remainingLogins;
        this.role = role; // 새로운 필드를 생성자에 추가
    }
}
