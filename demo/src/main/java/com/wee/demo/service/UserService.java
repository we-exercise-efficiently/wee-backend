package com.wee.demo.service;
import com.wee.demo.auth.AuthenticationFilter;
import com.wee.demo.domain.User;
import com.wee.demo.dto.request.UserLoginRequestDto;
import com.wee.demo.dto.response.UserSocialLoginResponseDto;
import com.wee.demo.dto.response.UserTokenResponseDto;
import com.wee.demo.repository.UserRepository;
import com.wee.demo.dto.request.UserRequestDto;
import com.wee.demo.dto.request.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMailService userMailService;
    private final Map<String, String> authCodes = new HashMap<>();

    @Transactional
    public UserRequestDto register(UserRequestDto userRequestDto) {
        String encodedPassword = passwordEncoder.encode(userRequestDto.getPassword());
        User user = User.builder()
                .email(userRequestDto.getEmail())
                .nickname(userRequestDto.getNickname())
                .password(encodedPassword)
                .build();
        User savedUser = userRepository.save(user);
        userRequestDto.setUserId(savedUser.getUserId());
        return userRequestDto;
    }
    public boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    public UserTokenResponseDto login(String email, String password, String loginType) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (loginType.equals("socialLogin")) {  // 소셜 로그인
            String accessToken = AuthenticationFilter.createAccessToken(user);
            String refreshToken = AuthenticationFilter.createRefreshToken(user);
            return new UserTokenResponseDto(accessToken, refreshToken);
        } else {  // 일반 로그인
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("wrong password");
            }
            String accessToken = AuthenticationFilter.createAccessToken(user);
            String refreshToken = AuthenticationFilter.createRefreshToken(user);
            return new UserTokenResponseDto(accessToken, refreshToken);
        }
    }
    public Optional<User> getUser(Long userId) {
        return userRepository.findByUserId(userId);
    }
    @Transactional
    public User updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user with user_id: " + userId));
        return userUpdateRequestDto.toEntity(user);
    }
    @Transactional
    public void withdrawUser(Long userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Not found user: " + userId));
        String encodedPassword = user.getPassword().replace("{bcrypt}", "");
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new IllegalArgumentException("Invalid password");
        }
        userRepository.deleteByUserId(userId);
    }
    public User registerUser(UserSocialLoginResponseDto userInfo, String platform) {
        System.out.println("userInfo: "+ userInfo);
        String email = userInfo.getEmail();
        if (email == null && platform.equals("Google")) {
            email = UUID.randomUUID().toString() + "@wee.com";
        }
        String nickname = userInfo.getNickname();
        User user = null;
        user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            UserRequestDto userRequestDto = new UserRequestDto();
            userRequestDto.setEmail(email);
            userRequestDto.setNickname(nickname);
            String password = UUID.randomUUID().toString();
            userRequestDto.setPassword(password);
            userRequestDto = register(userRequestDto);
            user = userRepository.findById(userRequestDto.getUserId()).orElse(null);
        }
        return user;
    }
    public void sendCodeToEmail(String toEmail) {
        String title = "WEE 이메일 인증 번호";
        String authCode = this.createCode();
        userMailService.sendEmail(toEmail, title, authCode);
        authCodes.put(toEmail, authCode);
    }
    private String createCode() {
        int length = 6;
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
    public boolean verifiedCode(String email, String authCode) {
        String storedAuthCode = authCodes.get(email);
        return storedAuthCode != null && storedAuthCode.equals(authCode);
    }
    public void resetPassword(UserLoginRequestDto userLoginRequestDto) {
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Not subscribed email"));
        String encodedPassword = passwordEncoder.encode(userLoginRequestDto.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }
}