package io.github.dynamicce.logger.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.dynamicce.logger.annotation.LogDetails;

@RestController
@Component
public class TestController {

    @LogDetails
    @GetMapping("/test")
    public String testMethod() {
        return "Test method called";
    }

}
