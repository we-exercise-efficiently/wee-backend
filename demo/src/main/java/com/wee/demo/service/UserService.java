package com.wee.demo.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wee.demo.domain.User;
import com.wee.demo.dto.request.UserSocialLoginRequestDto;
import com.wee.demo.dto.response.UserSocialResponseDto;
import com.wee.demo.dto.response.UserTokenResponseDto;
import com.wee.demo.repository.UserRepository;
import com.wee.demo.dto.request.UserRequestDto;
import com.wee.demo.dto.request.UserUpdateRequestDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.client.secret}")
    private String clientSecret;
    @Value("${kakao.redirect.url}")
    private String redirectUrl;
    @Value("${naver.client.id}")
    private String clientIdNaver;
    @Value("${naver.client.secret}")
    private String clientSecretNaver;
    @Value("${naver.redirect.url}")
    private String redirectUrlNaver;

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
    public UserTokenResponseDto login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("wrong password");
        }

        String accessToken = Jwts.builder()
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))  // 1시간
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
        String refreshToken = Jwts.builder()
                .setSubject(email)
                .claim("isRefreshToken", true)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 60 * 24 * 30 * 1000))  // 1개월
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
        return new UserTokenResponseDto(accessToken, refreshToken);
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

    public UserSocialResponseDto kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = getAccessToken(code);
        UserSocialResponseDto kakaoUserInfo = getKakaoUserInfo(accessToken);
        User kakaoUser = registerKakaoUser(kakaoUserInfo);
        Authentication authentication = forceLogin(kakaoUser);
        kakaoUserAuthorization(authentication, response);
        return kakaoUserInfo;
    }
    public String getAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUrl);
        body.add("code", code);
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        String resposeBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(resposeBody);
        return jsonNode.get("access_token").asText();
    }
    public UserSocialResponseDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long userId = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        return new UserSocialResponseDto(userId, email, nickname);
    }
    public User registerKakaoUser(UserSocialResponseDto kakaoUserInfo) {
        String kakaoEmail = kakaoUserInfo.getEmail();
        if (kakaoEmail == null) {
            kakaoEmail = UUID.randomUUID().toString() + "@wee.com";
        }
        String nickname = kakaoUserInfo.getNickname();
        User kakaoUser = userRepository.findByEmail(kakaoEmail)
                .orElse(null);
        System.out.println("kakaoUser: " + kakaoUser);
        if (kakaoUser == null) {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            kakaoUser = new User(nickname, kakaoEmail, encodedPassword);
            userRepository.save(kakaoUser);
        }
        return kakaoUser;
    }
    private Authentication forceLogin(User kakaoUser) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(kakaoUser, null, kakaoUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
    private void kakaoUserAuthorization(Authentication authentication, HttpServletResponse response) {
        User user = (User) authentication.getPrincipal();
        String token = Jwts.builder()
                .setSubject(user.getEmail())  // 'email'을 주제로 설정
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))  // 만료 시간을 1시간으로 설정
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))  // 시크릿 키로 서명
                .compact();
        response.addHeader("Authorization", "BEARER" + " " + token);
    }
    public String getCodeNaver() throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientIdNaver)
                .queryParam("redirect_uri", URLEncoder.encode(redirectUrlNaver, "UTF-8"))
                .queryParam("state", URLEncoder.encode("1234", "UTF-8"))
                .build();
        return uriComponents.toString();
    }
    public String getAccessTokenNaver(String code) throws UnsupportedEncodingException, JsonProcessingException {
        UriComponents uriComponents = UriComponentsBuilder
                    .fromUriString("https://nid.naver.com/oauth2.0/token")
                    .queryParam("grant_type", "authorization_code")
                    .queryParam("client_id", clientIdNaver)
                    .queryParam("client_secret", clientSecretNaver)
                    .queryParam("code", code)
                    .queryParam("state", URLEncoder.encode("1234", "UTF-8"))
                    .build();

            try {
                URL url = new URL(uriComponents.toString());
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                System.out.println("response: "+response);
//                return response.toString();
                String responseBody = response.toString();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.get("access_token").asText();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    public UserSocialResponseDto getUserInfoNaver(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                naverUserInfoRequest,
                String.class
        );
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseNode = objectMapper.readTree(responseBody).get("response");
        System.out.println("responseNode: "+responseNode);
        Long userId = responseNode.get("id").asLong();
        String email = responseNode.get("email").asText();
        String nickname = responseNode.get("name").asText();
        return new UserSocialResponseDto(userId, email, nickname);
    }
    public User registerNaverUser(UserSocialResponseDto naverUserInfo) {
        String kakaoEmail = naverUserInfo.getEmail();
        if (kakaoEmail == null) {
            kakaoEmail = UUID.randomUUID().toString() + "@wee.com";
        }
        String nickname = naverUserInfo.getNickname();
        User naverUser = userRepository.findByEmail(kakaoEmail)
                .orElse(null);
        System.out.println("kakaoUser: " + naverUser);
        if (naverUser == null) {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            naverUser = new User(nickname, kakaoEmail, encodedPassword);
            userRepository.save(naverUser);
        }
        return naverUser;
    }
    public String createToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))  // 1시간
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
    }
    public String createRefreshToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("isRefreshToken", true)
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 60 * 24 * 30 * 1000))  // 1개월
                .signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(jwtSecret.getBytes()))
                .compact();
    }
}