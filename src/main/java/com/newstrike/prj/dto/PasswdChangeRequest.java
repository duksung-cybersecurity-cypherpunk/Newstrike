package com.newstrike.prj.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data

public class PasswdChangeRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "새 비밀번호가 비어 있습니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,12}",
            message = "비밀번호를 영문, 숫자, 특수문자 포함 8~12자리로 설정해주세요.")
    private String newPasswd;

    @NotBlank(message = "새 비밀번호 확인이 비어 있습니다.")
    private String newPasswdCheck;
}
