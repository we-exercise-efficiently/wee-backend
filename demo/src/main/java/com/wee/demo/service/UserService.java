package com.wee.demo.service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wee.demo.domain.User;
import com.wee.demo.dto.response.UserSocialResponseDto;
import com.wee.demo.dto.response.UserTokenResponseDto;
import com.wee.demo.repository.UserRepository;
import com.wee.demo.dto.request.UserRequestDto;
import com.wee.demo.dto.request.UserUpdateRequestDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
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
    @Value("${google.client.id}")
    private String clientIdGoogle;
    @Value("${google.client.secret}")
    private String clientSecretGoogle;
    @Value("${google.redirect.url}")
    private String redirectUrlGoogle;

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
    public String getAccessTokenKakao(String code) throws JsonProcessingException {
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
    public UserSocialResponseDto getUserInfoKakao(String accessToken) throws JsonProcessingException {
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
    public User registerUserKakao(UserSocialResponseDto kakaoUserInfo) {
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
    public String getAccessTokenNaver(String code) throws UnsupportedEncodingException{
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
                if(responseCode==200) {
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
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
        Long userId = responseNode.get("id").asLong();
        String email = responseNode.get("email").asText();
        String nickname = responseNode.get("name").asText();
        return new UserSocialResponseDto(userId, email, nickname);
    }
    public User registerUserNaver(UserSocialResponseDto naverUserInfo) {
        String kakaoEmail = naverUserInfo.getEmail();
        if (kakaoEmail == null) {
            kakaoEmail = UUID.randomUUID().toString() + "@wee.com";
        }
        String nickname = naverUserInfo.getNickname();
        User naverUser = userRepository.findByEmail(kakaoEmail)
                .orElse(null);
        if (naverUser == null) {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            naverUser = new User(nickname, kakaoEmail, encodedPassword);
            userRepository.save(naverUser);
        }
        return naverUser;
    }
    public Map<String, String> getAccessTokenGoogle(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", clientIdGoogle);
        body.add("client_secret", clientSecretGoogle);
        body.add("redirect_uri", redirectUrlGoogle);
        body.add("code", code);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://oauth2.googleapis.com/token", requestEntity, String.class);
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
            System.out.println("response body: "+responseBody);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", jsonNode.get("access_token").asText());
            tokens.put("id_token", jsonNode.get("id_token").asText());
            return tokens;
        } else {
            System.out.println("Error occurred while requesting access token: " + responseEntity.getStatusCode());
            return null;
        }
    }
    public UserSocialResponseDto getUserInfoGoogle(Map<String, String> tokens) throws JsonProcessingException {
        String accessToken = tokens.get("access_token");
        String idToken = tokens.get("id_token");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> naverUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken,
                HttpMethod.GET,
                naverUserInfoRequest,
                String.class
        );
        String responseBody = response.getBody();
        System.out.println("response body: "+responseBody);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String userIdString = jsonNode.get("sub").asText();
        Long userId = (long) userIdString.hashCode();
        String email = UUID.randomUUID().toString() + "@wee.com";
        String nickname = jsonNode.get("name").asText();
        return new UserSocialResponseDto(userId, email, nickname);
    }
    public User registerUserGoogle(UserSocialResponseDto googleUserInfo) {
        String googleEmail = googleUserInfo.getEmail();
        String nickname = googleUserInfo.getNickname();
        User googleUser = userRepository.findByEmail(googleEmail)
                .orElse(null);
        if (googleUser == null) {
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);
            googleUser = new User(nickname, googleEmail, encodedPassword);
            userRepository.save(googleUser);
        }
        return googleUser;
    }
    public String createAccessToken(User user) {
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