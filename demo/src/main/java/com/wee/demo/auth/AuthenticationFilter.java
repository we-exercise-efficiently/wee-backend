package com.wee.demo.auth;

import com.wee.demo.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class AuthenticationFilter {
    private static String jwtSecret;
    @Value("${jwt.secret}")
    public void setJwtSecret(String jwtSecret) {
        AuthenticationFilter.jwtSecret = jwtSecret;
    }
    public static String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))  // 1시간
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
    }
    public static String createRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("isRefreshToken", true)
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))  // 1개월
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
    }
}

