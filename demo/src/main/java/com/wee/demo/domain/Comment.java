package com.wee.demo.domain;

import com.wee.demo.domain.community.Community;
import com.wee.demo.domain.enums.AnswerStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
@Table(name = "comment")
public class Comment {
    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;
    private String content;
    private AnswerStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;
    public void setUser(User user) {
        this.user = user;
        user.getCommentList().add(this);
    }

}
