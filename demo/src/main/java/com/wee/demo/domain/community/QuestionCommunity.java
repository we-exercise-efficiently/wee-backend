package com.wee.demo.domain.community;

import com.wee.demo.domain.enums.AnswerStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("Question")
@Table(name = "question_community")
public class QuestionCommunity extends Community{
    private String type;
    @Enumerated(EnumType.STRING)
    private AnswerStatus answerStatus;
}
