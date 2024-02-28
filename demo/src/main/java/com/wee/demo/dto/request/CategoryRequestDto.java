package com.wee.demo.dto.request;

import com.wee.demo.domain.Category;
import com.wee.demo.domain.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {
    private String categoryItem;
    private List<TaskRequestDto> taskList;
    public static CategoryRequestDto from(Category category, List<TaskRequestDto> taskList){
        return CategoryRequestDto.builder()
                .categoryItem(category.getCategoryItem())
                .taskList(taskList)
                .build();
    }
}
