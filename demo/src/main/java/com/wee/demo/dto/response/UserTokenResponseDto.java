package com.wee.demo.dto.response;

import com.wee.demo.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTokenResponseDto {
    private String accessToken;
    private String refreshToken;
    private User user;
}
