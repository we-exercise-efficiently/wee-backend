package com.wee.demo.controller;

import com.wee.demo.dto.request.QuestionDto;
import com.wee.demo.dto.response.QuestionResponseDto;
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

    //글 목록 조회
    @GetMapping("/list")
    public ResponseEntity getQuestions(@PageableDefault(page = 0, size = 10, sort = "createDate", direction = Sort.Direction.DESC)
                                           Pageable pageable, @RequestParam("filter") String filter) {
        //if(filter.equals("question"))
        //todo: filter를 이용한 운동질문방 외 조회
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
        System.out.println("questionDto id: " + questionDto.getQuestionId());
        QuestionResponseDto<QuestionDto> response = new QuestionResponseDto<>("200", "success", questionDto);
        return ResponseEntity.ok(response);
    }

    //운동질문방 글 수정
    @PatchMapping("/question/{questionId}")
    public ResponseEntity update(@PathVariable Long questionId, @RequestBody QuestionDto questionDto) {
        //Todo: PATCH 요청을 보낸 유저 == 게시물 작성자 체크 로직 추가
        QuestionDto updatedDto = questionService.update(questionId, questionDto);
        QuestionResponseDto<QuestionDto> response = new QuestionResponseDto<>("200", "success", updatedDto);
        return ResponseEntity.ok(response);
    }

    //운동질문방 글 삭제
    @DeleteMapping("/question/{questionId}")
    public ResponseEntity delete(@PathVariable Long questionId) {
        //Todo: DELETE 요청을 보낸 유저 == 게시물 작성자 체크 로직 추가
        questionService.delete(questionId);
        QuestionResponseDto<?> response = new QuestionResponseDto("200", "success", null);
        return ResponseEntity.ok(response);
    }
}
