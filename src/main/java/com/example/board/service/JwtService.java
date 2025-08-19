package com.example.board.service;

import com.example.board.model.entity.UserEntity;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private static final SecretKey key = Jwts.SIG.HS256.key().build();

    //accessToken을 발급해주는 메소드
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(userDetails.getUsername());
    }

    //accessToken으로 subject를 추출하는 메소드
    public String getUsername(String accessToken) {
        return getSubject(accessToken);

    }

    private String generateToken(String subject) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + (1000 * 60 * 60 * 3)); //3시간

        return Jwts.builder()
                .subject(subject)
                .signWith(key)
                .issuedAt(now)
                .expiration(exp)
                .compact(); //발생시점.expiration(exp) //만료시점.compact();
    }

    private String getSubject(String token) {

        try {
            String subject = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
            return subject;
        } catch (JwtException e) {
            log.error("JwtException", e);
            throw e;
        }
    }
}
