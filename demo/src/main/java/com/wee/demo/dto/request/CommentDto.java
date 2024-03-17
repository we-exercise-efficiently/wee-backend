package com.wee.demo.dto.request;

import com.wee.demo.domain.enums.AnswerStatus;
import com.wee.demo.domain.enums.CrewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long commentId;
    private String content;
    private AnswerStatus status;

    private Long userId;
    private String type; //커뮤니티방 종류 (question, crew, share)
}
