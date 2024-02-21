package com.wee.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSocialLoginResponseDto {
    private Long userId;
    private String nickname;
    private String email;
}
