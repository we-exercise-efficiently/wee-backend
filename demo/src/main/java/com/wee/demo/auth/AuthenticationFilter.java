package com.wee.demo.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationFilter {
    @Value("${jwt.secret}")
    private String jwtSecret;
    private final long jwtExpirationInMs = 3600000;



}
