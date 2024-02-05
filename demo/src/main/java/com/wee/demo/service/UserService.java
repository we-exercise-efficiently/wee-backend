package com.wee.demo.service;

import com.wee.demo.domain.User;
import com.wee.demo.domain.repository.UserRepository;
import com.wee.demo.dto.request.UserDto;
//import com.wee.demo.security.PasswordEncoderConfig;
import com.wee.demo.dto.request.UserUpdateDto;
import com.wee.demo.dto.response.UserLoginResponseDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    // 회원가입 | 로그인 | 회원정보 조회 | 회원정보 수정 | 회원정보 삭제
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private String jwtSecret = "dKj82k$#ks0KKS!29sAdjdk82k$#ks0KKS!29sAdjdk82k$#ks0KKS!29sAdjdk82k$#ks0KKS!29sAd";  // 차후 application.properties 또는 env에 추가될 예정
    @Transactional
    public UserDto register(UserDto userDto) {
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());

        User user = User.builder()
                .email(userDto.getEmail())
                .nickname(userDto.getNickname())
                .password(encodedPassword)
                .build();

        User savedUser = userRepository.save(user);
        userDto.setUserId(savedUser.getUserId());
        return userDto;
    }
    public UserLoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("wrong password");
        }
        // JWT 토큰 생성: 차후 별도 로직으로 분리해야함
        String token = Jwts.builder()
                .setSubject(email)
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
        UserLoginResponseDto response = new UserLoginResponseDto();
        response.setToken(token);
        response.setUser(user);
        return response;
    }
    public Optional<User> getUser(Long userId) {
        return userRepository.findByUserId(userId);
    }
    @Transactional
    public User updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new UsernameNotFoundException("Not found user with user_id: " + userId));
        return userUpdateDto.toEntity(user);
    }
    @Transactional
    public void withdrawUser(Long userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(()-> new IllegalArgumentException("Not found user: " + userId));
        String encodedPassword = user.getPassword().replace("{bcrypt}", "");
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }
        userRepository.deleteByUserId(userId);
    }
}

    // 로그인
//    private final JwtTokenProvider tokenProvider;
//    private final AuthenticationManager authenticationManager;
//    public UserResponseDto<LoginResponseDto> login(UserDto userDto) throws AuthenticationException {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(5userDto.getEmail(), userDto.getPassword())
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        User user = userRepository.findByEmail(userDto.getEmail())
//                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
//
//        // 토큰 생성
//        String jwt = tokenProvider.generateToken(authentication);
//        String refreshToken = tokenProvider.generateRefreshToken();
//
//        // 사용자 정보와 토큰을 포함하는 LoginResponseDto 생성
//        LoginResponseDto loginResponse = new LoginResponseDto(jwt, refreshToken);
//
//        // 사용자 정보와 LoginResponseDto를 포함하는 UserResponseDto 반환
//        UserResponseDto<LoginResponseDto> response = new UserResponseDto<>(
//                "200", "Login successful", loginResponse);
//
//        return response;
//    }




