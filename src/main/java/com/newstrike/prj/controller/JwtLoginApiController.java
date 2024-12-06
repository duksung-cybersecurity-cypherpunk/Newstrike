package com.newstrike.prj.controller;

import com.newstrike.prj.BaseResponse;
import com.newstrike.prj.BaseResponseStatus;
import com.newstrike.prj.auth.JwtTokenUtil;
import com.newstrike.prj.dto.EmailCheckRequest;
import com.newstrike.prj.dto.JoinRequest;
import com.newstrike.prj.dto.LoginRequest;
import com.newstrike.prj.dto.PasswdChangeRequest;
import com.newstrike.prj.domain.Users;
import com.newstrike.prj.service.SubscribeService;
import com.newstrike.prj.service.UserService;
import com.newstrike.prj.service.UserService.InvalidPasswordException;
import com.newstrike.prj.service.UserService.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class JwtLoginApiController {
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final SubscribeService subscribeService;

    @PostMapping("/join")
    public ResponseEntity<BaseResponse<Map<String, String>>> join(@Valid @RequestBody JoinRequest joinRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR, errors));
        }

        // email 중복 체크
        if (userService.checkEmailDuplicate(joinRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new BaseResponse<>(BaseResponseStatus.POST_USERS_EXISTS_EMAIL));
        }

        // password와 passwordCheck가 같은지 체크
        if (!joinRequest.getPasswd().equals(joinRequest.getPasswdCheck())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.INVALID_PASSWORD_CHECK));
        }

        // 회원가입 처리
        userService.join(joinRequest);

        // 회원가입 후 구독 처리
        try {
            // 구독 처리 - 이미 구독된 상태라면 새로 구독하지 않음
            subscribeService.subscribe(joinRequest.getEmail(), joinRequest.getNickname());
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Email already exists")) {
                return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.POST_USERS_EXISTS_EMAIL), HttpStatus.CONFLICT);
            }
            return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(BaseResponseStatus.SUCCESS));
    }


    @PostMapping("/login")
    public ResponseEntity<BaseResponse<String>> login(@RequestBody LoginRequest loginRequest,
                                                    HttpServletResponse response, HttpServletRequest request) {
        try {
            // 로그인 처리
            Users user = userService.login(loginRequest);

            // 로그인 성공 시 JWT 토큰 생성
            long expireTimeMs = 1000 * 60 * 60 * 24 * 7; // 7일
            String jwtToken = jwtTokenUtil.createToken(user.getId(), expireTimeMs);

            // 성공 응답과 함께 JWT 토큰 반환
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, jwtToken));

        } catch (UserNotFoundException e) {
            // 이메일이 없는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(BaseResponseStatus.POST_USERS_NON_EXIST_EMAIL, e.getMessage()));

        } catch (InvalidPasswordException e) {
            // 비밀번호가 일치하지 않는 경우
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new BaseResponse<>(BaseResponseStatus.LOGIN_FAILED, e.getMessage()));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<BaseResponse<String>> userInfo(Authentication auth) {
        Users loginUser = userService.getLoginUserByEmail(auth.getName());
        String userInfo = String.format("email : %s\nnickname : %s\nrole : %s",
                loginUser.getEmail(), loginUser.getNickname(), loginUser.getRole().name());

        return ResponseEntity.ok(new BaseResponse<>(userInfo));
    }
    
    @PostMapping("/duplicateCheck")
    public ResponseEntity<BaseResponse<String>> checkEmailDuplicate(@RequestBody EmailCheckRequest emailCheckRequest) {
        boolean isDuplicate = userService.checkEmailDuplicate(emailCheckRequest.getEmail());
        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.POST_USERS_EXISTS_EMAIL));
        }
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, "사용 가능한 이메일입니다."));
    }

    @PutMapping("/changePasswd")
    public ResponseEntity<BaseResponse<String>> changePasswd(@Valid @RequestBody PasswdChangeRequest passwdChangeRequest, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR, errors.toString()));
            }
        
            // 새 비밀번호와 비밀번호 확인이 일치하는지 확인
            if (!passwdChangeRequest.getNewPasswd().equals(passwdChangeRequest.getNewPasswdCheck())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.INVALID_PASSWORD_CHECK));
            }
        
            // UserService를 사용하여 비밀번호 변경
            boolean isChanged = userService.changePasswd(passwdChangeRequest.getEmail(), passwdChangeRequest.getNewPasswd());
        
            if (!isChanged) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.RESPONSE_ERROR, "비밀번호 변경에 실패했습니다."));
            }
        
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS, "비밀번호가 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();  // 예외를 콘솔에 출력하여 문제의 원인을 확인합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse<>(BaseResponseStatus.RESPONSE_ERROR, "서버에서 오류가 발생했습니다."));
        }
    }

    @GetMapping("/authentication-fail")
    public String authenticationFail() {
        return "errorPage/authenticationFail";
    }

    @GetMapping("/authorization-fail")
    public String authorizationFail() {
        return "errorPage/authorizationFail";
    }
}
