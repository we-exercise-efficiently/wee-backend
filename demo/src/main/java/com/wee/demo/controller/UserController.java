package com.wee.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wee.demo.domain.User;
import com.wee.demo.dto.request.UserSocialLoginRequestDto;
import com.wee.demo.dto.response.UserSocialResponseDto;
import com.wee.demo.dto.response.UserTokenResponseDto;
import com.wee.demo.repository.UserRepository;
import com.wee.demo.dto.request.UserRequestDto;
import com.wee.demo.dto.request.UserLoginRequestDto;
import com.wee.demo.dto.request.UserUpdateRequestDto;
import com.wee.demo.dto.response.UserResponseDto;
import com.wee.demo.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wee/user")
public class UserController {
    private final UserService customUserDetailsService;
    private final UserService userServiceImpl;
    private final UserRepository userRepository;
    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto<UserRequestDto>> register(@RequestBody UserRequestDto userRequestDto) {
        UserRequestDto registeredUserRequestDto = customUserDetailsService.register(userRequestDto);
        UserResponseDto<UserRequestDto> response = new UserResponseDto<>(200, "success", registeredUserRequestDto);
        System.out.println(registeredUserRequestDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto<UserTokenResponseDto>> login(@RequestBody UserLoginRequestDto loginDto) {
        UserTokenResponseDto userTokenResponseDto = userServiceImpl.login(loginDto.getEmail(), loginDto.getPassword());
        UserResponseDto<UserTokenResponseDto> response = new UserResponseDto<>(200, "success", userTokenResponseDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+userTokenResponseDto.getAccessToken());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
    @GetMapping("/mypage")
    public ResponseEntity<UserResponseDto<User>> getUser(@RequestParam Long userId, @RequestHeader("Authorization") String authorizationHeader) {
        Optional<User> user = userServiceImpl.getUser(userId);
        if (user.isPresent()) {
            UserResponseDto<User> response = new UserResponseDto<>(200, "success", user.get());
            return ResponseEntity.ok(response);
        } else {
            UserResponseDto<User> response = new UserResponseDto<>(400, "Not found user", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    @PatchMapping("/mypage")
    public ResponseEntity<UserResponseDto<User>> updateUser(@RequestParam Long userId, @RequestBody UserUpdateRequestDto userUpdateRequestDto, @RequestHeader("Authorization") String authorizationHeader) {
        User user = userServiceImpl.updateUser(userId, userUpdateRequestDto);
        UserResponseDto<User> response = new UserResponseDto<>(200, "success", user);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/mypage")
    public ResponseEntity<UserResponseDto<?>> withdrawUser(@RequestParam Long userId, @RequestParam String password, @RequestHeader("Authorization") String authorizationHeader) {
        userServiceImpl.withdrawUser(userId, password);
        UserResponseDto<?> response = new UserResponseDto<>(200, "success", null);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/login/kakao/callback")
    public UserSocialResponseDto kakaoLoginCallback (@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return userServiceImpl.kakaoLogin(code, response);
    }
    @PostMapping("/login/kakao")
    public ResponseEntity<UserResponseDto<UserTokenResponseDto>> kakaoLogin(@RequestHeader("code") String code) throws JsonProcessingException {
        String accessToken = userServiceImpl.getAccessToken(code);
        UserSocialResponseDto kakaoUser = userServiceImpl.getKakaoUserInfo(accessToken);
        User registeredKakaoUser = userServiceImpl.registerKakaoUser(kakaoUser);
        String jwtAccessToken = userServiceImpl.createToken(registeredKakaoUser);
        String jwtRefreshToken = userServiceImpl.createRefreshToken(registeredKakaoUser);
        UserTokenResponseDto userTokenResponseDto = new UserTokenResponseDto(jwtAccessToken, jwtRefreshToken);
        UserResponseDto<UserTokenResponseDto> response = new UserResponseDto<>(200, "success", userTokenResponseDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+jwtAccessToken);
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
}
