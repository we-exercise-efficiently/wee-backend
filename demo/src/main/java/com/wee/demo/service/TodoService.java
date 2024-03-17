package com.wee.demo.service;

import com.wee.demo.domain.*;
import com.wee.demo.dto.request.DiaryRequestDto;
import com.wee.demo.dto.request.TaskRequestDto;
import com.wee.demo.dto.request.TodoRequestDto;
import com.wee.demo.dto.response.ResultType;
import com.wee.demo.dto.response.TodoDto;
import com.wee.demo.dto.response.TodoResponseDto;
import com.wee.demo.repository.TaskRepository;
import com.wee.demo.repository.TodoRepository;
import com.wee.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public TodoResponseDto createTodo(Long userId, TodoRequestDto todoRequestDto) {
        List<Long> taskIdList = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(("사용자가 존재하지 않습니다.")));
        if (todoRepository.findByToday(todoRequestDto.getToday()).isPresent()) {
            Todo todo = todoRepository.findByToday(todoRequestDto.getToday()).orElseThrow(() -> new IllegalArgumentException(("todo 가 존재하지 않습니다.")));
            todoRequestDto.getTaskList().stream().forEach(taskRequestDto -> {
                Task task = Task.builder()
                        .content(taskRequestDto.getContent())
                        .isCompleted(taskRequestDto.isCompleted())
                        .build();

                taskRepository.save(task);
                taskIdList.add(task.getId());
                todo.getTaskList().add(task); // Todo에 Task 추가
            });
            user.getTodoList().add(todo);
            return TodoResponseDto.from(todo.getId(), taskIdList);
        }
        else {
            Todo todo = Todo.builder()
                    .user(user)
                    .today(todoRequestDto.getToday())
                    .taskList(new ArrayList<>())
                    .build();
            todoRepository.save(todo);
            todoRequestDto.getTaskList().stream().forEach(taskRequestDto -> {
                Task task = Task.builder()
                        .content(taskRequestDto.getContent())
                        .isCompleted(taskRequestDto.isCompleted())
                        .build();

                taskRepository.save(task);
                taskIdList.add(task.getId());
                todo.getTaskList().add(task); // Todo에 Task 추가
            });
            user.getTodoList().add(todo);
            return TodoResponseDto.from(todo.getId(), taskIdList);
        }
    }
    @Transactional
    public Long createDiary(Long userId, DiaryRequestDto diaryRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(("사용자가 존재하지 않습니다.")));
        if (todoRepository.findByToday(diaryRequestDto.getToday()).isPresent()) {
            Todo todo = todoRepository.findByToday(diaryRequestDto.getToday()).orElseThrow(() -> new IllegalArgumentException(("todo 가 존재하지 않습니다.")));
            todo.setImage(diaryRequestDto.getImage());
            todo.setDiary(diaryRequestDto.getDiary());
            return todo.getId();
        }
        else {
            Todo todo = Todo.builder()
                    .user(user)
                    .today(diaryRequestDto.getToday())
                    .diary(diaryRequestDto.getDiary())
                    .image(diaryRequestDto.getImage())
                    .build();
            todoRepository.save(todo);
            user.getTodoList().add(todo);
            return todo.getId();
        }
    }
    @Transactional
    public Long updateTodo(Long todoId, TodoDto todoRequestDto) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException(("todo 가 존재하지 않습니다.")));
        todo.setDiary(todoRequestDto.getDiary());
        todo.setImage(todoRequestDto.getImage());
        return todo.getId();
    }

    @Transactional
    public Long updateTask(Long taskId, TaskRequestDto taskReqeustDto) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException(("task 가 존재하지 않습니다.")));
        task.setContent(taskReqeustDto.getContent());
        task.setCompleted(taskReqeustDto.isCompleted());
        return task.getId();
    }
    @Transactional
    public ResultType deleteTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException(("todo 가 존재하지 않습니다.")));
        todoRepository.delete(todo);
        return ResultType.SUCCESS;
    }

    @Transactional
    public ResultType deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException(("task 가 존재하지 않습니다.")));
        taskRepository.delete(task);
        return ResultType.SUCCESS;
    }
    public TodoDto findTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId).orElseThrow(() -> new IllegalArgumentException(("todo 가 존재하지 않습니다.")));
        List<Task> taskList = todo.getTaskList();
        List<TaskRequestDto> taskResponseList = new ArrayList<>();
        for(Task task : taskList) {
            taskResponseList.add(TaskRequestDto.from(task));
        }
        return TodoDto.from(todo, taskResponseList);
    }
    @Transactional
    public ResultType checkTask(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException(("task 가 존재하지 않습니다.")));
        task.setCompleted(!task.isCompleted());
        return ResultType.SUCCESS;
    }

}
