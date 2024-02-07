package com.wee.demo.controller;

import com.wee.demo.domain.community.QuestionCommunity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/comm")
public class CommunityController {

    //운동질문방 글 작성
    @PostMapping("/question")
    public ResponseEntity write(@RequestBody QuestionCommunity qc) throws Exception{
        log.info("get parameter" + qc.getId());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
