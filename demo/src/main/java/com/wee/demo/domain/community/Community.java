package com.wee.demo.domain.community;

import com.wee.demo.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@Data
@Table(name = "community")
public abstract class Community {
    @Id @GeneratedValue
    @Column(name = "community_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private String title;
    private String content;
    private int likes;
    private Date createDate;
    private int hit;
    private int commentCnt;
    private String image;
    public void setUser(User user) {
        this.user = user;
        user.getCommunityList().add(this);
    }
}
