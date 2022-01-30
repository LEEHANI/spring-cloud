package com.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final Environment env;

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
        return "Hi, there. This is a message from member service.";
    }

    @GetMapping("/load-balance")
    public String loadBalance() {
        log.info("spring-cloud LB member test");
        return "Member service LB!";
    }

    @GetMapping("/health-check")
    public String statue() {
        return String.format("config-client service" +
                ", port(local.server.port) = " + env.getProperty("local.server.port") +
                ", default.content = " + env.getProperty("default.content")
        );
    }
}
