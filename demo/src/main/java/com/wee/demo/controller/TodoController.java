package com.wee.demo.controller;

import com.wee.demo.dto.request.DiaryRequestDto;
import com.wee.demo.dto.request.TaskRequestDto;
import com.wee.demo.dto.request.TodoRequestDto;
import com.wee.demo.dto.response.ResultType;
import com.wee.demo.dto.response.TodoDto;
import com.wee.demo.dto.response.TodoResponseDto;
import com.wee.demo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/wee")
public class TodoController {
    private final TodoService todoService;

    @PostMapping("/todo/{userId}")
    public TodoResponseDto createTodo(@PathVariable Long userId, @RequestBody TodoRequestDto todoRequestDto){
        return todoService.createTodo(userId, todoRequestDto);
    }
    @PostMapping("/diary/{userId}")
    public Long createDiary(@PathVariable Long userId, @RequestBody DiaryRequestDto diaryRequestDto){
        return todoService.createDiary(userId, diaryRequestDto);
    }
    @PatchMapping("/todo/{todoId}")
    public Long updateTodo(@PathVariable Long todoId, @RequestBody TodoDto todoRequestDto){
        return todoService.updateTodo(todoId, todoRequestDto);
    }
    @PatchMapping("/task/{taskId}")
    public Long updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto taskReqeustDto){
        return todoService.updateTask(taskId, taskReqeustDto);
    }
    @DeleteMapping("/todo/{todoId}")
    public ResultType deleteTodo(@PathVariable Long todoId){
        return todoService.deleteTodo(todoId);
    }

    @DeleteMapping("/task/{taskId}")
    public ResultType deleteTask(@PathVariable Long taskId){
        return todoService.deleteTask(taskId);
    }

    @GetMapping("/todo/{todoId}")
    public TodoDto findTodo(@PathVariable Long todoId) {
        return todoService.findTodo(todoId);
    }

    @PatchMapping("/todo/check/{taskId}")
    public ResultType checkTask(@PathVariable Long taskId) {
        return todoService.checkTask(taskId);
    }

}
