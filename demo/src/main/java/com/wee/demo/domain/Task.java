package com.wee.demo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "task")
public class Task {
    @Id @GeneratedValue
    @Column(name = "task_id")
    private Long id;
    private String content;
    private boolean isCompleted;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    public void setCategory(Category category) {
        this.category = category;
        category.getTaskList().add(this);
    }
}
