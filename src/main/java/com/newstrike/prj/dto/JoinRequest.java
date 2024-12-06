package com.newstrike.prj.dto;

import com.newstrike.prj.domain.UserRole;
import com.newstrike.prj.domain.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "닉네임이 비어 있습니다.")
    private String nickname;

    @NotBlank(message = "이메일이 비어 있습니다.")
    @Email(message = "유효하지 않은 이메일입니다.")
    private String email;

    @NotBlank(message = "비밀번호가 비어 있습니다.")
    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,12}",
            message = "비밀번호를 영문, 숫자, 특수문자 포함 8~12자리로 설정해주세요.")
    private String passwd;
    private String passwdCheck;

    public Users toEntity(String encodedPasswd) {
        return Users.builder()
                .email(this.email)
                .passwd(encodedPasswd)
                .nickname(this.nickname)
                .role(UserRole.USER)
                .build();
    }
}
