package com.wee.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponseDto {
    private Long categoryId;
    private List<Long> taskId;
    public static CategoryResponseDto from(Long categoryId, List<Long> taskId){
        return CategoryResponseDto.builder()
                .categoryId(categoryId)
                .taskId(taskId)
                .build();
    }
}
