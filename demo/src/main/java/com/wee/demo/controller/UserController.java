package com.wee.demo.controller;

import com.wee.demo.domain.User;
import com.wee.demo.dto.response.UserTokenResponseDto;
import com.wee.demo.repository.UserRepository;
import com.wee.demo.dto.request.UserDto;
import com.wee.demo.dto.request.UserLoginDto;
import com.wee.demo.dto.request.UserUpdateDto;
import com.wee.demo.dto.response.UserResponseDto;
import com.wee.demo.service.UserService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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
    public ResponseEntity<UserResponseDto<UserDto>> register(@RequestBody com.wee.demo.dto.request.UserDto userDto) {
        UserDto registeredUserDto = customUserDetailsService.register(userDto);
        UserResponseDto<UserDto> response = new UserResponseDto<>(200, "success", registeredUserDto);
        System.out.println(registeredUserDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto<UserTokenResponseDto>> login(@RequestBody UserLoginDto loginDto) {
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
    public ResponseEntity<UserResponseDto<User>> updateUser(@RequestParam Long userId, @RequestBody UserUpdateDto userUpdateDto, @RequestHeader("Authorization") String authorizationHeader) {
        User user = userServiceImpl.updateUser(userId, userUpdateDto);
        UserResponseDto<User> response = new UserResponseDto<>(200, "success", user);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/mypage")
    public ResponseEntity<UserResponseDto<?>> withdrawUser(@RequestParam Long userId, @RequestParam String password, @RequestHeader("Authorization") String authorizationHeader) {
        userServiceImpl.withdrawUser(userId, password);
        UserResponseDto<?> response = new UserResponseDto<>(200, "success", null);
        return ResponseEntity.ok(response);
    }
}