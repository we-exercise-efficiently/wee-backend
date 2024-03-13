package com.wee.demo.service;

import com.wee.demo.domain.Comment;
import com.wee.demo.domain.User;
import com.wee.demo.domain.community.Community;
import com.wee.demo.domain.community.CrewCommunity;
import com.wee.demo.domain.community.QuestionCommunity;
import com.wee.demo.dto.request.CommentDto;
import com.wee.demo.dto.request.CrewDto;
import com.wee.demo.dto.request.QuestionDto;
import com.wee.demo.repository.CommentRepository;
import com.wee.demo.repository.CrewRepository;
import com.wee.demo.repository.QuestionRepository;
import com.wee.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final CrewRepository crewRepository;

    @Transactional
    public CommentDto write(Long commId, CommentDto commentDto) {
        User user = userRepository.findById(commentDto.getUserId()).orElseThrow(() -> new IllegalArgumentException(("사용자가 존재하지 않습니다.")));
        String type = commentDto.getType();
        Community community = null;
        if(type.equals("question")) {
            community = questionRepository.findById(commId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        } else if(type.equals("crew")) {
            community = crewRepository.findById(commId).orElseThrow(() -> new IllegalArgumentException(("게시물이 존재하지 않습니다.")));
        }

        Comment comment = Comment.builder()
                .user(user)
                .community(community)
                .content(commentDto.getContent())
                .status(commentDto.getStatus())
                .build();

        Comment savedOne = commentRepository.save(comment);

        commentDto.setCommentId(savedOne.getId());
        return commentDto;
    }

    @Transactional
    public QuestionDto getQuestionComments(QuestionDto questionDto) {
        List<CommentDto> result = new ArrayList<>();
        List<Comment> comments = commentRepository.findAll();
        for(Comment comment : comments) {
            Community community = comment.getCommunity();
            if(questionDto.getQuestionId() == community.getId()) {
                CommentDto commentDto = CommentDto.builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .status(comment.getStatus())
                        .userId(comment.getUser().getUserId())
                        .type("question")
                        .build();
                result.add(commentDto);
            }
        }
        questionDto.setComments(result);
        return questionDto;
    }

    @Transactional
    public CrewDto getCrewComments(CrewDto crewDto) {
        List<CommentDto> result = new ArrayList<>();
        List<Comment> comments = commentRepository.findAll();
        for(Comment comment : comments) {
            Community community = comment.getCommunity();
            if(crewDto.getCrewId() == community.getId()) {
                CommentDto commentDto = CommentDto.builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .status(comment.getStatus())
                        .userId(comment.getUser().getUserId())
                        .type("crew")
                        .build();
                result.add(commentDto);
            }
        }
        crewDto.setComments(result);
        return crewDto;
    }

    @Transactional
    public CommentDto update(Long commId, Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException(("댓글이 존재하지 않습니다.")));
        comment.setContent(commentDto.getContent());
        Comment updatedOne = commentRepository.save(comment);
        return commentDto;
    }

    @Transactional
    public void delete(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException(("댓글이 존재하지 않습니다.")));
        commentRepository.deleteById(commentId);
    }
}
