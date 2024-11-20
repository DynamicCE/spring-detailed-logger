package io.github.dynamicce.spring_detailed_logger.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.dynamicce.spring_detailed_logger.annotation.LogDetails;

@RestController
public class TestController {

    @LogDetails
    @GetMapping("/test")
    public String testMethod() {
        return "Test successful!";
    }
}
