package com.wee.demo.controller;

import com.wee.demo.dto.request.CommentDto;
import com.wee.demo.dto.request.CrewDto;
import com.wee.demo.dto.request.QuestionDto;
import com.wee.demo.dto.response.CommentResponseDto;
import com.wee.demo.dto.response.CrewResponseDto;
import com.wee.demo.dto.response.QuestionResponseDto;
import com.wee.demo.service.CommentService;
import com.wee.demo.service.CrewService;
import com.wee.demo.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wee/comm")
public class CommunityController {

    private final QuestionService questionService;
    private final CrewService crewService;
    private final CommentService commentService;

    //운동질문방 글 목록 조회
    @GetMapping("/question/list")
    public ResponseEntity getQuestions(@PageableDefault(page = 0, size = 10, sort = "createDate", direction = Sort.Direction.DESC)
                                           Pageable pageable) {
        Page<QuestionDto> questions = questionService.getQuestions(pageable);
        QuestionResponseDto<Page<QuestionDto>> response = new QuestionResponseDto<>("200", "success", questions);
        return ResponseEntity.ok(response);
    }

    //운동질문방 글 작성
    @PostMapping("/question/{userId}")
    public ResponseEntity writeQuestion(@PathVariable Long userId, @RequestBody QuestionDto questionDto) {
        log.info("userId: " + userId);
        log.info("QuestionDto: " + questionDto.getTitle());

        QuestionDto writtenDto = questionService.write(userId, questionDto);
        QuestionResponseDto<QuestionDto> response = new QuestionResponseDto<>("200", "success", writtenDto);
        return ResponseEntity.ok(response);
    }

    //운동질문방 글 상세페이지 조회
    @GetMapping("/question/{questionId}")
    public ResponseEntity getQuestion(@PathVariable Long questionId) {
        QuestionDto questionDto = questionService.getQuestion(questionId);
        QuestionDto resultDto = commentService.getQuestionComments(questionDto);
        System.out.println("questionDto id: " + resultDto.getQuestionId());
        QuestionResponseDto<QuestionDto> response = new QuestionResponseDto<>("200", "success", resultDto);
        return ResponseEntity.ok(response);
    }

    //운동질문방 글 수정
    @PatchMapping("/question/{questionId}")
    public ResponseEntity updateQuestion(@PathVariable Long questionId, @RequestBody QuestionDto questionDto) {
        //Todo: PATCH 요청을 보낸 유저 == 게시물 작성자 체크 로직 추가
        QuestionDto updatedDto = questionService.update(questionId, questionDto);
        QuestionResponseDto<QuestionDto> response = new QuestionResponseDto<>("200", "success", updatedDto);
        return ResponseEntity.ok(response);
    }

    //운동질문방 글 삭제
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity deleteQuestion(@PathVariable Long questionId) {
        //Todo: DELETE 요청을 보낸 유저 == 게시물 작성자 체크 로직 추가
        questionService.delete(questionId);
        QuestionResponseDto<?> response = new QuestionResponseDto("200", "success", null);
        return ResponseEntity.ok(response);
    }

    //크루모집방 글 목록 조회
    @GetMapping("/crew/list")
    public ResponseEntity getCrews(@PageableDefault(page = 0, size = 10, sort = "createDate", direction = Sort.Direction.DESC)
                                       Pageable pageable) {
        Page<CrewDto> crews = crewService.getCrews(pageable);
        CrewResponseDto<Page<CrewDto>> response = new CrewResponseDto<>("200", "success", crews);
        return ResponseEntity.ok(response);
    }

    //크루모집방 글 작성
    @PostMapping("/crew/{userId}")
    public ResponseEntity writeCrew(@PathVariable Long userId, @RequestBody CrewDto crewDto) {
        log.info("userId: " + userId);
        log.info("CrewDto: " + crewDto.getTitle());

        CrewDto writtenDto = crewService.write(userId, crewDto);
        CrewResponseDto<CrewDto> response = new CrewResponseDto<>("200", "success", writtenDto);
        return ResponseEntity.ok(response);
    }

    //크루모집방 글 상세페이지 조회
    @GetMapping("/crew/{crewId}")
    public ResponseEntity getCrew(@PathVariable Long crewId) {
        CrewDto crewDto = crewService.getCrew(crewId);
        CrewDto resultDto = commentService.getCrewComments(crewDto);
        System.out.println("crewDto id: " + resultDto.getCrewId());
        CrewResponseDto<CrewDto> response = new CrewResponseDto<>("200", "success", resultDto);
        return ResponseEntity.ok(response);
    }

    //크루모집방 글 수정
    @PatchMapping("/crew/{crewId}")
    public ResponseEntity updateCrew(@PathVariable Long crewId, @RequestBody CrewDto crewDto) {
        //Todo: PATCH 요청을 보낸 유저 == 게시물 작성자 체크 로직 추가
        CrewDto updatedDto = crewService.update(crewId, crewDto);
        CrewResponseDto<CrewDto> response = new CrewResponseDto<>("200", "success", updatedDto);
        return ResponseEntity.ok(response);
    }

    //크루모집방 글 삭제
    @DeleteMapping("/crew/{crewId}")
    public ResponseEntity deleteCrew(@PathVariable Long crewId) {
        //Todo: DELETE 요청을 보낸 유저 == 게시물 작성자 체크 로직 추가
        crewService.delete(crewId);
        CrewResponseDto<CrewDto> response = new CrewResponseDto<>("200", "success", null);
        return ResponseEntity.ok(response);
    }

    //댓글 작성
    @PostMapping("/{commId}/comments")
    public ResponseEntity writeComment(@PathVariable Long commId, @RequestBody CommentDto commentDto) {
        log.info("commId: " + commId);
        log.info("CommentDto: " + commentDto.getContent());

        CommentDto writtenDto = commentService.write(commId, commentDto);
        CommentResponseDto<CommentDto> response = new CommentResponseDto<>("200", "success", writtenDto);
        return ResponseEntity.ok(response);
    }

    //댓글 수정
    @PatchMapping("/{commId}/comments/{commentId}")
    public ResponseEntity updateComment(@PathVariable Long commId, @PathVariable Long commentId, @RequestBody CommentDto commentDto) {
        //Todo: PATCH 요청을 보낸 유저 == 댓글 작성자 체크 로직 추가
        CommentDto updatedDto = commentService.update(commId, commentId, commentDto);
        CommentResponseDto<CommentDto> response = new CommentResponseDto<>("200", "success", updatedDto);
        return ResponseEntity.ok(response);
    }

    //댓글 삭제
    @DeleteMapping("/{commId}/comments/{commentId}")
    public ResponseEntity deleteComment(@PathVariable Long commId, @PathVariable Long commentId) {
        //Todo: DELETE 요청을 보낸 유저 == 댓글 작성자 체크 로직 추가
        commentService.delete(commentId);
        CommentResponseDto<CommentDto> response = new CommentResponseDto<>("200", "success", null);
        return ResponseEntity.ok(response);
    }
}
