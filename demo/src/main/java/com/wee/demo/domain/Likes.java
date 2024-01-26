package com.wee.demo.domain;


import com.wee.demo.domain.community.Community;
import com.wee.demo.domain.enums.LikeType;
import jakarta.persistence.*;
import lombok.*;
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "likes")
public class Likes {
    @Id @GeneratedValue
    @Column(name = "like_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;
    @Enumerated(EnumType.STRING)
    private LikeType type;
    public void setUser(User user) {
        this.user = user;
        user.getLikeList().add(this);
    }

}
