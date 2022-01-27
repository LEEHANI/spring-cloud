package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to member service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("member-request") String header) {
        log.info("header : {}", header);
        return "Hello member service header";
    }

    @GetMapping("/check")
    public String check() {
        return "Hi, there. This is a message from Member Service.";
    }
}
