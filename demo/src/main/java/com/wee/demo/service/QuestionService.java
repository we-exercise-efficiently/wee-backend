package com.wee.demo.service;

import com.wee.demo.domain.User;
import com.wee.demo.domain.community.QuestionCommunity;
import com.wee.demo.dto.request.QuestionDto;
import com.wee.demo.repository.QuestionRepository;
import com.wee.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    @Transactional
    public QuestionDto write(Long userId, QuestionDto questionDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException(("사용자가 존재하지 않습니다.")));

        QuestionCommunity questionCommunity = QuestionCommunity.builder()
                .user(user)
                .title(questionDto.getTitle())
                .content(questionDto.getContent())
                .likes(questionDto.getLikes())
                .createDate(questionDto.getCreateDate())
                .hit(questionDto.getHit())
                .commentCnt(questionDto.getCommentCnt())
                .image(questionDto.getImage())
                .type(questionDto.getType())
                .answerStatus(questionDto.getAnswerStatus())
                .build();

        QuestionCommunity savedOne = questionRepository.save(questionCommunity);
        questionDto.setQuestionId(savedOne.getId());
        return questionDto;
    }

    @Transactional
    public QuestionDto getQuestion(Long questionId) {
        QuestionCommunity questionCommunity = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        //Optional<QuestionCommunity> questionCommunity = questionRepository.findByCommunityId(questionId);
        QuestionDto questionDto = QuestionDto.builder()
                .questionId(questionCommunity.getId())
                .title(questionCommunity.getTitle())
                .content(questionCommunity.getContent())
                .likes(questionCommunity.getLikes())
                .createDate(questionCommunity.getCreateDate())
                .hit(questionCommunity.getHit())
                .commentCnt(questionCommunity.getCommentCnt())
                .image(questionCommunity.getImage())
                .type(questionCommunity.getType())
                .answerStatus(questionCommunity.getAnswerStatus())
                .build();

        return questionDto;
    }

    @Transactional
    public QuestionDto update(Long questionId, QuestionDto questionDto) {
        QuestionCommunity questionCommunity = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        questionCommunity.setTitle(questionDto.getTitle());
        questionCommunity.setContent(questionDto.getContent());
        questionCommunity.setCreateDate(questionDto.getCreateDate());
        questionCommunity.setImage(questionDto.getImage());
        questionCommunity.setType(questionDto.getType());
        QuestionCommunity updatedOne = questionRepository.save(questionCommunity);
        return questionDto;
    }

    @Transactional
    public void delete(Long questionId) {
        QuestionCommunity questionCommunity = questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        questionRepository.deleteById(questionId);
    }
}
