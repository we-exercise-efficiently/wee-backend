package com.wee.demo.dto.request;

import com.wee.demo.domain.Task;
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
}
