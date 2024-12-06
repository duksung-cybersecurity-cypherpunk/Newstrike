package com.newstrike.prj.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    private final SecretKey secretKey;


    public JwtTokenUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        logger.info("JWT Secret Key initialized. Key length: {}, Hash: {}", secret.length(), secret.hashCode());
        logger.info("JWT Secret Key: {}", secret); // 추가된 로깅 코드
    }

    // JWT Token 발급
    public String createToken(String id, long expireTimeMs) {
        logger.info("Creating JWT token with key hash: {}", Arrays.hashCode(secretKey.getEncoded()));
        Claims claims = Jwts.claims().setSubject(id);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs)) // 만료 시간 설정 확인
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        logger.info("Generated JWT token: {}", token);
        return token;
    }

    // JWT Token에서 ID 추출
    public String getId(String token) {
        logger.info("Extracting ID from token: {}", token);

        try {
            // 디버깅을 위한 로깅 추가
            logger.debug("Extracting ID with secretKey: {}", Arrays.toString(secretKey.getEncoded()));

            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error extracting ID from token: {}", e.getMessage());
            throw e;
        }
    }

    // Token 만료 여부 확인
    public boolean isExpired(String token) {
        logger.info("Checking if token is expired with key hash: {}", Arrays.hashCode(secretKey.getEncoded()));
        try {
            Date expiration = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature while checking expiration: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error checking if token is expired", e);
            throw e;
        }
    }

   


}