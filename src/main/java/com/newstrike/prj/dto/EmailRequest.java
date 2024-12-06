package com.newstrike.prj.dto;

import lombok.Data;

@Data
public class EmailRequest {

    // 이메일 주소
    private String email;
    // 인증 코드
    private String verifyCode;
}
