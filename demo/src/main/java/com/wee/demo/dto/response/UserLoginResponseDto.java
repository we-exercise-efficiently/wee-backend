package com.wee.demo.dto.response;

import com.wee.demo.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserLoginResponseDto {
    private String token;
    private User user;
}
