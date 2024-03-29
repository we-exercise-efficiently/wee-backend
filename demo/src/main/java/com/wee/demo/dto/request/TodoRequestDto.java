package com.wee.demo.dto.request;

import com.wee.demo.domain.Task;
import com.wee.demo.domain.Todo;
import com.wee.demo.dto.response.CategoryResponseDto;
import com.wee.demo.dto.response.TodoResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequestDto {
    private List<TaskRequestDto> taskList;
    private Date today;

    public static TodoRequestDto from(Todo todo, List<TaskRequestDto> taskList){
        return TodoRequestDto.builder()
                .today(todo.getToday())
                .taskList(taskList)
                .build();
    }
}
