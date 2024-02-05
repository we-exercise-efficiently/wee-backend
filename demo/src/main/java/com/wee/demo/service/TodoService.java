package com.wee.demo.service;

import com.wee.demo.domain.*;
import com.wee.demo.dto.request.CategoryRequestDto;
import com.wee.demo.dto.request.TaskRequestDto;
import com.wee.demo.dto.request.TodoRequestDto;
import com.wee.demo.dto.response.TodoResponseDto;
import com.wee.demo.repository.CategoryRepository;
import com.wee.demo.repository.TaskRepository;
import com.wee.demo.repository.TodoRepository;
import com.wee.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final TaskRepository taskRepository;
    @Transactional
    public TodoResponseDto createTodo(Long userId, TodoRequestDto todoRequestDto) {
        List<Long> categoryId = new ArrayList<>();
        List<Long> taskId = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(("사용자가 존재하지 않습니다.")));
        Todo todo = Todo.builder()
                .user(user)
                .today(todoRequestDto.getToday())
                .diary(todoRequestDto.getDiary())
                .image(todoRequestDto.getImage())
                .categoryList(new ArrayList<>())
                .build();
        todoRepository.save(todo);
        todoRequestDto.getCategoryList().stream().forEach(categoryRequestDto -> {
            Category category = Category.builder()
                    .todo(todo)
                    .categoryItem(categoryRequestDto.getCategoryItem())
                    .taskList(new ArrayList<>())
                    .build();
            categoryRepository.save(category);
            categoryRequestDto.getTaskList().stream().forEach(taskRequestDto -> {
                Task task = Task.builder()
                        .category(category)
                        .content(taskRequestDto.getContent())
                        .isCompleted(taskRequestDto.isCompleted())
                        .build();

                taskRepository.save(task);  // Category에 Task 추가
                taskId.add(task.getId());
                category.getTaskList().add(task); // Category에 Task 추가
            });
            todo.getCategoryList().add(category);
            categoryId.add(category.getId());
        });
        user.getTodoList().add(todo);
        return TodoResponseDto.from(todo.getId(), categoryId, taskId);
    }
    @Transactional
    public Long updateTodo(Long todoId, TodoRequestDto todoRequestDto) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException(("todo 가 존재하지 않습니다.")));
        todo.setDiary(todoRequestDto.getDiary());
        todo.setImage(todoRequestDto.getImage());
        return todo.getId();
    }
    @Transactional
    public Long updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new IllegalArgumentException(("category 가 존재하지 않습니다.")));
        category.setCategoryItem(categoryRequestDto.getCategoryItem());
        return category.getId();
    }
    @Transactional
    public Long updateTask(Long taskId, TaskRequestDto taskReqeustDto) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException(("task 가 존재하지 않습니다.")));
        task.setContent(taskReqeustDto.getContent());
        task.setCompleted(taskReqeustDto.isCompleted());
        return task.getId();
    }
}
