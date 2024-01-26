package com.wee.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "routine")
public class Routine {
    @Id @GeneratedValue
    @Column(name = "routine_id")
    private Long id;
    private String image;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    public void setUser(User user) {
        this.user = user;
        user.getRoutineList().add(this);
    }
}
