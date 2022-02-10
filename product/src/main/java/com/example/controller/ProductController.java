package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/product")
public class ProductController {

    @GetMapping("/welcome")
    public String welcome() {
        return "Welcome to product service";
    }

    @GetMapping("/message")
    public String message(@RequestHeader("product-request") String header) {
        log.info("header : {}", header);
        return "Hello product service header";
    }

    @GetMapping("/check")
    public String check() {
        return "Hi, there. This is a message from product service.";
    }
}
