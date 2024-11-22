package io.github.dynamicce.logger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.dynamicce.logger.annotation.LogDetails;

@RestController
public class TestController {

    @LogDetails
    @GetMapping("/test")
    public String testMethod() {
        return "Test successful!";
    }

    @LogDetails
    @GetMapping("/error")
    public String errorMethod() {
        throw new RuntimeException("Test error!");
    }
}
