package com.newstrike.prj.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRequest {
    private String nickname;
    private String passwd;
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,12}",
            message = "비밀번호를 영문, 숫자, 특수문자 포함 8~12자리로 설정해주세요.")
    private String newPasswd;
    private String newPasswdCheck;
}
