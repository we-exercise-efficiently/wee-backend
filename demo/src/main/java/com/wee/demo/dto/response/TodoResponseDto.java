package com.wee.demo.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {
    private Long todoId;
    private List<Long> taskIdList;
    public static TodoResponseDto from(Long todoId, List<Long> taskIdList){
        return TodoResponseDto.builder()
                .todoId(todoId)
                .taskIdList(taskIdList)
                .build();
    }
}
