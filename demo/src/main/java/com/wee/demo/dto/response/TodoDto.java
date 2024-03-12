package com.wee.demo.dto.response;

import com.wee.demo.domain.Todo;
import com.wee.demo.dto.request.TaskRequestDto;
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
public class TodoDto {
    private List<TaskRequestDto> taskList;
    private String diary;
    private Date today;
    private String image;
    public static TodoDto from(Todo todo, List<TaskRequestDto> taskList){
        return TodoDto.builder()
                .diary(todo.getDiary())
                .today(todo.getToday())
                .image(todo.getImage())
                .taskList(taskList)
                .build();
    }
}
