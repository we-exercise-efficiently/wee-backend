package com.wee.demo.dto.request;

import lombok.*;
import java.time.LocalDateTime;
import com.wee.demo.domain.User;
import com.wee.demo.domain.Info;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long userId;
    private String email;
    private String nickname;
    private String password;
    private Boolean gender;
    private Double height;
    private Double weight;
    private Double bodyFat;
    private String goal;
    private String interest;
    private Integer level;
    @Builder
    public User toEntity() {
        Info info = Info.builder()
                .gender(this.gender)
                .height(this.height)
                .weight(this.weight)
                .bodyFat(this.bodyFat)
                .goal(this.goal)
                .interest(this.interest)
                .level(this.level)
                .build();

        return User.builder()
                .userId(this.userId)
                .email(this.email)
                .nickname(this.nickname)
                .password(this.password)
                .info(info) // Info 객체를 User 객체에 설정
                .build();
    }
}

