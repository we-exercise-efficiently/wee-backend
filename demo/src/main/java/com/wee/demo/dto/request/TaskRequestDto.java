package com.wee.demo.dto.request;

import com.wee.demo.domain.Task;
import com.wee.demo.domain.Todo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
    private String content;
    private boolean isCompleted;
    public static TaskRequestDto from(Task task){
        return TaskRequestDto.builder()
                .content(task.getContent())
                .isCompleted(task.isCompleted())
                .build();
    }
}
