package com.wee.demo.auth;

import com.wee.demo.domain.User;
import com.wee.demo.dto.response.UserResponseDto;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

public class AuthenticationFilter extends OncePerRequestFilter {
    private JwtProperties jwtProperties;
    @Value("${jwt.secret}")
    private String jwtSecret;
    public AuthenticationFilter(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(jwtProperties.ACCESS_HEADER_STRING);
        if (header == null || !header.startsWith(jwtProperties.TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.replace(jwtProperties.TOKEN_PREFIX, "");
        try {
            Jwts.parser().setSigningKey(Base64.getEncoder().encodeToString(jwtSecret.getBytes())).parseClaimsJws(token);
        } catch (JwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
