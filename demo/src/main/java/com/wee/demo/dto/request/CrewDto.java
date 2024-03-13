package com.wee.demo.dto.request;

import com.wee.demo.domain.enums.AnswerStatus;
import com.wee.demo.domain.enums.CrewStatus;
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
public class CrewDto {
    private Long crewId;
    private String title;
    private String content;
    private int likes;
    private Date createDate;
    private int hit;
    private int commentCnt;
    private String image;

    private String period; //진행기간
    private String location;
    private String type;
    private int headCount;
    private CrewStatus crewStatus;

    private List<CommentDto> comments;
}
