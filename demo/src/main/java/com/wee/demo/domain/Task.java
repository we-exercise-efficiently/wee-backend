package com.wee.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Data
@Table(name = "task")
public class Task {
    @Id @GeneratedValue
    @Column(name = "task_id")
    private Long id;
    private String content;
    private boolean isCompleted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Todo todo;
    public void setCategory(Todo todo) {
        this.todo = todo;
        todo.getTaskList().add(this);
    }
}
