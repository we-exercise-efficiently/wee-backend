package com.wee.demo.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {
    private Long todoId;
    private List<Long> categoryId;
    private List<Long> taskId;
    public static TodoResponseDto from(Long todoId, List<Long> categoryId, List<Long> taskId){
        return TodoResponseDto.builder()
                .todoId(todoId)
                .categoryId(categoryId)
                .taskId(taskId)
                .build();
    }


}
