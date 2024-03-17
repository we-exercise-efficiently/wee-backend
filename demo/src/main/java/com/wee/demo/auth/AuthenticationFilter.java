package com.wee.demo.auth;

import com.wee.demo.domain.User;
import com.wee.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
public class AuthenticationFilter {
    private static String jwtSecret;
    private static ApplicationContext ctx;
    @Value("${jwt.secret}")
    public void setJwtSecret(String jwtSecret) {
        AuthenticationFilter.jwtSecret = jwtSecret;
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        AuthenticationFilter.ctx = applicationContext;
    }
    public static String createAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_TOKEN_EXPIRATION_TIME))  // 1시간
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
    }
    public static String createRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .claim("user_id", user.getUserId())
                .setExpiration(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_TOKEN_EXPIRATION_TIME))  // 1개월
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
    }
    public static Optional<User> getUserByToken(String token) {
        try {
            String userId = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            System.out.println("userId: "+userId);
            UserRepository userRepository = ctx.getBean(UserRepository.class);
            return userRepository.findById(Long.parseLong(userId));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}

