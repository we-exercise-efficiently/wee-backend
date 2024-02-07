package com.wee.demo.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoResponseDto {
    private Long todoId;
    private List<CategoryResponseDto> categoryIdList;
    public static TodoResponseDto from(Long todoId, List<CategoryResponseDto> categoryIdList){
        return TodoResponseDto.builder()
                .todoId(todoId)
                .categoryIdList(categoryIdList)
                .build();
    }
}
