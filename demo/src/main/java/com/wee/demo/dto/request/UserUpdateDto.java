package com.wee.demo.dto.request;

import com.wee.demo.domain.Info;
import com.wee.demo.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
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
    public User toEntity(User existingUser) {
        Info info = Info.builder()
                .gender(this.gender)
                .height(this.height)
                .weight(this.weight)
                .bodyFat(this.bodyFat)
                .goal(this.goal)
                .interest(this.interest)
                .level(this.level)
                .build();

        existingUser.setNickname(this.nickname);
        existingUser.setPassword(this.password);
        existingUser.setInfo(info);

        return existingUser;
    }
}
