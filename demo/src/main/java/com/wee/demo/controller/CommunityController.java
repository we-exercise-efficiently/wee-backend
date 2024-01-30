package com.wee.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@RequestMapping("/comm")
public class CommunityController {

    //운동질문방 글 작성
    @PostMapping("/question")
    public ResponseEntity write(@RequestBody HashMap<String, Object> requestJsonHashMap) throws Exception{
        System.out.println("write complete");
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
