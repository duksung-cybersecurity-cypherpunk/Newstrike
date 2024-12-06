package com.newstrike.prj.controller;

import com.newstrike.prj.BaseResponse;
import com.newstrike.prj.BaseResponseStatus;
import com.newstrike.prj.auth.JwtTokenUtil;
import com.newstrike.prj.domain.Users;
import com.newstrike.prj.dto.UserRequest;
import com.newstrike.prj.dto.UserResponse;
import com.newstrike.prj.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class UserController {

    private final UserService userService;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenUtil jwtTokenUtil;  // JwtTokenUtil 주입받기

    @GetMapping
    public ResponseEntity<BaseResponse<UserResponse>> getMyPage(
            @RequestHeader(value = "Authorization", required = false) String token, // Authorization 헤더에서 토큰 추출
            HttpServletResponse response) {

        if (token == null || !token.startsWith("Bearer ")) {
            // 토큰이 없거나 Bearer로 시작하지 않으면 UNAUTHORIZED_REQUEST 응답
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_REQUEST));
        }

        token = token.substring(7); // "Bearer " 이후의 실제 토큰 값만 추출

        if (jwtTokenUtil.isExpired(token)) {  // 인스턴스를 통해 호출
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.INVALID_JWT));
        }

        String userId = jwtTokenUtil.getId(token);  // 인스턴스를 통해 호출
        Users user = userService.getUserById(userId);

        UserResponse userResponse = new UserResponse(
                user.getEmail(),
                user.getNickname(),
                user.getGrade_count(),
                user.getGrade(),
                user.getNext_grade(),
                user.getRemainingLogins(),
                user.getRole()
        );

        return ResponseEntity.ok(new BaseResponse<>(userResponse));
    }

    @PutMapping("change")
public ResponseEntity<BaseResponse<?>> fixMypage(
        @RequestHeader(value = "Authorization", required = false) String token,  // Authorization 헤더에서 토큰 추출
        @Valid @RequestBody UserRequest userRequest,
        BindingResult bindingResult) {
    try {
        // 검증 오류가 있는지 확인
        // if (bindingResult.hasErrors()) {
        //     Map<String, String> errors = new HashMap<>();
        //     bindingResult.getFieldErrors().forEach(error ->
        //             errors.put(error.getField(), error.getDefaultMessage()));
        //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR, errors));
        // }

        // JWT 토큰이 없거나 유효하지 않은 경우 처리
        if (token == null || !token.startsWith("Bearer ") || jwtTokenUtil.isExpired(token.substring(7))) {
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.INVALID_JWT));
        }

        token = token.substring(7); // "Bearer " 이후의 실제 토큰 값만 추출

        String userId = jwtTokenUtil.getId(token);
        Users user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.POST_USERS_NON_EXIST_EMAIL));
        }

        // 비밀번호 업데이트가 필요한 경우 검증 로직 수행
        boolean isPasswordChange = false;
        if (userRequest.getPasswd() != null && !userRequest.getPasswd().isEmpty() &&
                userRequest.getNewPasswd() != null && !userRequest.getNewPasswd().isEmpty() &&
                userRequest.getNewPasswdCheck() != null && !userRequest.getNewPasswdCheck().isEmpty()) {

            if (!encoder.matches(userRequest.getPasswd(), user.getPasswd())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(BaseResponseStatus.INVALID_EXISTING_PASSWORD));
            }

            if (!userRequest.getNewPasswd().equals(userRequest.getNewPasswdCheck())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new BaseResponse<>(BaseResponseStatus.INVALID_PASSWORD_CHECK));
            }

            // 비밀번호가 성공적으로 검증된 경우
            isPasswordChange = true;
        }

        // 닉네임 변경이 필요한 경우만 처리
        if (userRequest.getNickname() != null && !userRequest.getNickname().isEmpty()) {
            user.setNickname(userRequest.getNickname());
        }

        // 업데이트된 사용자 정보를 저장하기 위해 updateUser 메서드 호출
        UserResponse updatedUser = userService.updateUser(token, userRequest, isPasswordChange);
        return ResponseEntity.ok(new BaseResponse<>(updatedUser));

    } catch (Exception e) {
        e.printStackTrace();  // 예외 스택 트레이스를 출력하여 디버깅 정보를 얻습니다.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse<>(BaseResponseStatus.RESPONSE_ERROR, null));
    }
}

    @PostMapping("/unsubscribe")
    public ResponseEntity<BaseResponse<String>> unsubscribe(
            @RequestParam("email") String email) {

        boolean result = userService.unsubscribe(email);
        if (result) {
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
        } else {
            return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<String>> deleteUser(
            @RequestHeader(value = "Authorization", required = false) String token,  // Authorization 헤더에서 토큰 추출
            HttpServletResponse response) {

        if (token == null || !token.startsWith("Bearer ")) {
            return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_REQUEST), HttpStatus.UNAUTHORIZED);
        }

        token = token.substring(7); // "Bearer " 이후의 실제 토큰 값만 추출

        String userId = jwtTokenUtil.getId(token);  // 인스턴스를 통해 호출
        boolean result = userService.deleteUserById(userId);
        if (result) {
            return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.SUCCESS));
        } else {
            return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR, "User deletion failed"), HttpStatus.BAD_REQUEST);
        }
    }
}