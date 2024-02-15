package com.wee.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLoginRequestDto {
    private String provider; // 소셜 로그인 제공자
    private String accessToken;
}
