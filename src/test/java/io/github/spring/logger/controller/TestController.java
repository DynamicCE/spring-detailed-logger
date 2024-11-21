package io.github.spring.logger.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.github.spring.logger.annotation.LogDetails;

@RestController
public class TestController {
    
    @LogDetails
    @GetMapping("/test")
    public String testMethod() {
        return "Test successful!";
    }
    
    @LogDetails
    public String throwErrorMethod() {
        throw new RuntimeException("Test exception");
    }
} 