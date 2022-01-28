package com.example.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ConfigClientController {

    private final Environment env;

    @GetMapping("/health-check")
    public String statue() {
        return String.format("config-client service" +
                ", port(local.server.port) = " + env.getProperty("local.server.port") +
                ", default.content = " + env.getProperty("default.content")
        );
    }
}
