package com.wee.demo.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
@Table(name = "category")
public class Category {
    @Id @GeneratedValue
    @Column(name = "category_id")
    private Long id;
    private String categoryItem;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Task> taskList = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id")
    private Todo todo;
    public void setTodo(Todo todo) {
        this.todo = todo;
        todo.getCategoryList().add(this);
    }
}
