package com.wee.demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wee.demo.domain.User;
import com.wee.demo.dto.response.UserSocialLoginResponseDto;
import com.wee.demo.dto.response.UserTokenResponseDto;
import com.wee.demo.dto.request.UserRequestDto;
import com.wee.demo.dto.request.UserLoginRequestDto;
import com.wee.demo.dto.request.UserUpdateRequestDto;
import com.wee.demo.dto.response.UserResponseDto;
import com.wee.demo.service.UserMailService;
import com.wee.demo.service.UserSocialLoginService;
import com.wee.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wee/user")
public class UserController {
    private final UserService customUserDetailsService;
    private final UserService userServiceImpl;
    private final UserSocialLoginService userSocialLoginService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto<UserRequestDto>> register(@RequestBody UserRequestDto userRequestDto) {
        UserRequestDto registeredUserRequestDto = customUserDetailsService.register(userRequestDto);
        UserResponseDto<UserRequestDto> response = new UserResponseDto<>(200, "success", registeredUserRequestDto);
        System.out.println(registeredUserRequestDto);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/register/checkemail")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = customUserDetailsService.isEmailExists(email);
        if (exists) {
            return ResponseEntity.ok(new UserResponseDto<>(201, "duplicated.", null));
        } else {
            return ResponseEntity.ok(new UserResponseDto<>(200, "unduplicated", null));
        }
    }
    @PostMapping("/login")
    public ResponseEntity<UserResponseDto<UserTokenResponseDto>> login(@RequestBody UserLoginRequestDto loginDto) {
        UserTokenResponseDto userTokenResponseDto = userServiceImpl.login(loginDto.getEmail(), loginDto.getPassword(), "login");
        UserResponseDto<UserTokenResponseDto> response = new UserResponseDto<>(200, "success", userTokenResponseDto);
        String userId = userServiceImpl.findUserIdByEmail(loginDto.getEmail());
        HttpHeaders headers = new HttpHeaders();
        headers.add("user_id", userId);
        headers.add("Authorization", "Bearer "+userTokenResponseDto.getAccessToken());
        headers.add("refresh_token", userTokenResponseDto.getRefreshToken());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
    @GetMapping("/mypage")
    public ResponseEntity<UserResponseDto<User>> getUser(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Optional<User> user = userServiceImpl.getUserByToken(token);
        if (user.isPresent()) {
            UserResponseDto<User> response = new UserResponseDto<>(200, "success", user.get());
            return ResponseEntity.ok(response);
        } else {
            UserResponseDto<User> response = new UserResponseDto<>(400, "Not found user", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
    @PatchMapping("/mypage")
    public ResponseEntity<UserResponseDto<User>> updateUser(@RequestBody UserUpdateRequestDto userUpdateRequestDto, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        Optional<User> userOptional = userServiceImpl.getUserByToken(token);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        User user = userServiceImpl.updateUser(userOptional.get().getUserId(), userUpdateRequestDto);
        UserResponseDto<User> response = new UserResponseDto<>(200, "success", user);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/mypage")
    public ResponseEntity<UserResponseDto<?>> withdrawUser(@RequestParam String password, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // "Bearer " 문자열 제거
        Optional<User> userOptional = userServiceImpl.getUserByToken(token);
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        userServiceImpl.withdrawUser(userOptional.get().getUserId(), password);
        UserResponseDto<?> response = new UserResponseDto<>(200, "success", null);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login/kakao")
    public ResponseEntity<UserResponseDto<UserTokenResponseDto>> kakaoLogin(@RequestHeader("code") String code) throws JsonProcessingException {
        String accessToken = userSocialLoginService.getAccessTokenKakao(code);
        UserSocialLoginResponseDto kakaoUser = userSocialLoginService.getUserInfoKakao(accessToken);
        User registeredKakaoUser = userServiceImpl.registerUser(kakaoUser, "Kakao");
        // 강제 로그인 처리
        UserTokenResponseDto userTokenResponseDto = userServiceImpl.login(registeredKakaoUser.getEmail(), registeredKakaoUser.getPassword(), "socialLogin");
        UserResponseDto<UserTokenResponseDto> response = new UserResponseDto<>(200, "success", userTokenResponseDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+userTokenResponseDto.getAccessToken());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
    @GetMapping("/login/naver/url")  // for test
    public String naverGetUrl() throws UnsupportedEncodingException {
        return userSocialLoginService.getUrlNaver("code");
    }
    @PostMapping("/login/naver")
    public ResponseEntity<UserResponseDto<UserTokenResponseDto>> naverLogin(@RequestHeader("code") String code) throws Exception {
        String accessToken = userSocialLoginService.getAccessTokenNaver(code);
        UserSocialLoginResponseDto naverUser = userSocialLoginService.getUserInfoNaver(accessToken);
        User registeredNaverUser = userServiceImpl.registerUser(naverUser, "Naver");
        UserTokenResponseDto userTokenResponseDto = userServiceImpl.login(registeredNaverUser.getEmail(), registeredNaverUser.getPassword(), "socialLogin");
        UserResponseDto<UserTokenResponseDto> response = new UserResponseDto<>(200, "success", userTokenResponseDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+userTokenResponseDto.getAccessToken());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
    @PostMapping("/login/google")
    public ResponseEntity<UserResponseDto<UserTokenResponseDto>> googleLogin(@RequestParam("code") String code) throws JsonProcessingException {
        Map<String, String> tokens = userSocialLoginService.getAccessTokenGoogle(code);
        UserSocialLoginResponseDto googleUser = userSocialLoginService.getUserInfoGoogle(tokens);
        User registeredGoogleUser = userServiceImpl.registerUser(googleUser, "Google");
        UserTokenResponseDto userTokenResponseDto = userServiceImpl.login(registeredGoogleUser.getEmail(), registeredGoogleUser.getPassword(), "socialLogin");
        UserResponseDto<UserTokenResponseDto> response = new UserResponseDto<>(200, "success", userTokenResponseDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+userTokenResponseDto.getAccessToken());
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }
    @PostMapping("/sendmail")
    public ResponseEntity sendMessage(@RequestParam("mail") String mail) {
        userServiceImpl.sendCodeToEmail(mail);
        UserResponseDto<String> responseDto = new UserResponseDto<>();
        responseDto.setCode(200);
        responseDto.setMessage("Success");
        responseDto.setData("Verification code sent to " + mail);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    @GetMapping("/verifymail")
    public ResponseEntity verificationEmail(@RequestParam("mail") String mail, @RequestParam("code") String code) {
        boolean isVerified = userServiceImpl.verifiedCode(mail, code);
        UserResponseDto<Boolean> responseDto = new UserResponseDto<>();
        responseDto.setCode(isVerified ? 200 : 400);
        responseDto.setMessage(isVerified ? "Success" : "Verification failed");
        responseDto.setData(isVerified);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    @PatchMapping("/resetpassword")
    public ResponseEntity<UserResponseDto<Void>> resetPassword(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        userServiceImpl.resetPassword(userLoginRequestDto);
        UserResponseDto<Void> responseDto = new UserResponseDto<>(200, "Success", null);
        return ResponseEntity.ok(responseDto);
    }
}
