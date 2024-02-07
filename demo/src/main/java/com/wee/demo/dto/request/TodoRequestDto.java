package com.wee.demo.dto.request;

import com.wee.demo.domain.Task;
import com.wee.demo.domain.Todo;
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
    private List<CategoryRequestDto> categoryList;
    private String diary;
    private Date today;
    private String image;
}
