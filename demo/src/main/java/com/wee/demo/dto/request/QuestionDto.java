package com.wee.demo.dto.request;

import com.wee.demo.domain.enums.AnswerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String title;
    private String content;
    private int likes;
    private Date createDate;
    private int hit;
    private int commentCnt;
    private String image;

    private String type;
    private AnswerStatus answerStatus;
}
