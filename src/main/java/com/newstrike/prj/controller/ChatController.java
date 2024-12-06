package com.newstrike.prj.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newstrike.prj.auth.JwtTokenUtil;
import com.newstrike.prj.dto.ChatRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final RestTemplate restTemplate;
    private Map<String, List<String>> chatHistory = new HashMap<>(); // 사용자별 대화 내역을 저장하는 Map

    public ChatController(JwtTokenUtil jwtTokenUtil, RestTemplateBuilder restTemplateBuilder) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.restTemplate = restTemplateBuilder.build();
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestHeader("Authorization") String token, @RequestBody ChatRequest request) {
        logger.info("Received prompt: {}", request.getPrompt());

        String userId = jwtTokenUtil.getId(token.substring(7));

        chatHistory.putIfAbsent(userId, new ArrayList<>());
        if (request.getPrompt() != null) {
            chatHistory.get(userId).add("User: " + request.getPrompt());
        } else {
            chatHistory.get(userId).add("User: null");
        }

        String fastApiUrl = "http://127.0.0.1:8000/chat";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(fastApiUrl, entity, String.class);

        // 응답에서 JSON 파싱
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonResponse = mapper.readTree(response.getBody());
            String parsedResponse = jsonResponse.get("response").asText(); // "response" 필드를 가져와 파싱
            
            chatHistory.get(userId).add("Bot: " + parsedResponse); // 파싱한 값을 추가
        } catch (Exception e) {
            logger.error("Error parsing response: ", e);
            chatHistory.get(userId).add("Bot: " + response.getBody()); // 에러 발생 시 원본 추가
        }

        return ResponseEntity.ok(chatHistory.get(userId));
    }

}