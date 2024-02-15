package com.wee.demo.domain.community;

import com.wee.demo.domain.enums.AnswerStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@DiscriminatorValue("Question")
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "question_community")
public class QuestionCommunity extends Community{
    private String type;
    @Enumerated(EnumType.STRING)
    private AnswerStatus answerStatus;
}
