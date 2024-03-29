package com.wee.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
@Table(name = "todo")
public class Todo {
    @Id @GeneratedValue
    @Column(name = "todo_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Date today;
    private String diary;
    private String image;
    @OneToMany(mappedBy = "todo", cascade = CascadeType.ALL)
    private List<Task> taskList = new ArrayList<>();

    public void setUser(User user) {
        this.user = user;
        user.getTodoList().add(this);
    }
}
