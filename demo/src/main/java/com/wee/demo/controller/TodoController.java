package com.wee.demo.controller;

import com.wee.demo.domain.Todo;
import com.wee.demo.dto.request.CategoryRequestDto;
import com.wee.demo.dto.request.TaskRequestDto;
import com.wee.demo.dto.request.TodoRequestDto;
import com.wee.demo.dto.response.ResultType;
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
    @PatchMapping("/todo/{todoId}")
    public Long updateTodo(@PathVariable Long todoId, @RequestBody TodoRequestDto todoRequestDto){
        return todoService.updateTodo(todoId, todoRequestDto);
    }
    @PatchMapping("/category/{categoryId}")
    public Long updateCategory(@PathVariable Long categoryId,  @RequestBody CategoryRequestDto categoryRequestDto){
        return todoService.updateCategory(categoryId, categoryRequestDto);
    }
    @PatchMapping("/task/{taskId}")
    public Long updateTask(@PathVariable Long taskId, @RequestBody TaskRequestDto taskReqeustDto){
        return todoService.updateTask(taskId, taskReqeustDto);
    }
    @DeleteMapping("/todo/{todoId}")
    public ResultType deleteTodo(@PathVariable Long todoId){
        return todoService.deleteTodo(todoId);
    }
    @DeleteMapping("/category/{categoryId}")
    public ResultType deleteCategory(@PathVariable Long categoryId){
        return todoService.deleteCategory(categoryId);
    }
    @DeleteMapping("/task/{taskId}")
    public ResultType deleteTask(@PathVariable Long taskId){
        return todoService.deleteTask(taskId);
    }

    @GetMapping("/todo/{todoId}")
    public TodoRequestDto findTodo(@PathVariable Long todoId) {
        return todoService.findTodo(todoId);
    }

    @PatchMapping("/todo/check/{taskId}")
    public ResultType checkTask(@PathVariable Long taskId) {
        return todoService.checkTask(taskId);
    }

}
