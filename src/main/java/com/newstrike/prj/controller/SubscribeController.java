package com.newstrike.prj.controller;

import com.newstrike.prj.domain.Subscriber;
import com.newstrike.prj.dto.SubscriptionRequest;
import com.newstrike.prj.service.SubscribeService;


import com.newstrike.prj.BaseResponse;
import com.newstrike.prj.BaseResponseStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/v1")
public class SubscribeController {

    @Autowired
    private SubscribeService subscribeService;

    @PostMapping("/subscribe")
    public ResponseEntity<BaseResponse<Subscriber>> subscribe(@RequestBody SubscriptionRequest request) {
        try {
            if (request.getEmail() == null) {
                return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.POST_USER_EMPTY_EMAIL), HttpStatus.BAD_REQUEST);
            }
            if (request.getNickname() == null) {
                return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.POST_USER_EMPTY_NICKNAME), HttpStatus.BAD_REQUEST);
            }

            Subscriber newSubscriber = subscribeService.subscribe(request.getEmail(), request.getNickname());
            return new ResponseEntity<>(new BaseResponse<>(newSubscriber), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Email already exists")) {
                return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.POST_USERS_EXISTS_EMAIL), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(new BaseResponse<>(BaseResponseStatus.REQUEST_ERROR), HttpStatus.BAD_REQUEST);
        }
    }
}
