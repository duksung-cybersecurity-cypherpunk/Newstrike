package com.newstrike.prj.controller;

import com.newstrike.prj.BaseResponse;
import com.newstrike.prj.BaseResponseStatus;
import com.newstrike.prj.dto.EmailRequest;
import com.newstrike.prj.service.EmailService;
import com.newstrike.prj.service.UserService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/email")
public class EmailController {
    private final EmailService emailService;
    private final UserService userService;


    // 인증코드 메일 발송
    @PostMapping("/send")
    public ResponseEntity<BaseResponse<String>> mailSend(@RequestBody EmailRequest emailDto) throws MessagingException {
        log.info("EmailController.mailSend()");
        emailService.sendEmail(emailDto.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(BaseResponseStatus.SUCCESS, "인증코드가 발송되었습니다."));
    }

    @PostMapping("/pwsend")
    public ResponseEntity<BaseResponse<String>> pwMailSend(@RequestBody EmailRequest emailDto) throws MessagingException {
        log.info("EmailController.mailSend()");
        boolean isDuplicate = userService.checkEmailDuplicate(emailDto.getEmail());
        if (isDuplicate) {
            emailService.sendEmail(emailDto.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(BaseResponseStatus.SUCCESS, "인증코드가 발송되었습니다."));
        }else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(BaseResponseStatus.POST_USERS_NON_EXIST_EMAIL));
        }
    }

    // 인증코드 인증
    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<String>> verify(@RequestBody EmailRequest emailDto) {
        log.info("EmailController.verify()");
        boolean isVerify = emailService.verifyEmailCode(emailDto.getEmail(), emailDto.getVerifyCode());
        if (isVerify) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(BaseResponseStatus.SUCCESS, "인증이 완료되었습니다."));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BaseResponse<>(BaseResponseStatus.UNAUTHORIZED_REQUEST, "인증 실패하셨습니다."));
        }
    }
}
