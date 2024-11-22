package io.github.dynamicce.logger.aspect;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.TestPropertySource;

import io.github.dynamicce.logger.annotation.LogDetails;
import io.github.dynamicce.logger.config.TestConfig;
import io.github.dynamicce.logger.controller.TestController;

@SpringBootTest(classes = {TestConfig.class})
@ExtendWith(OutputCaptureExtension.class)
@TestPropertySource(properties = {"logging.level.io.github.dynamicce=DEBUG",
        "logging.level.operation-logs=DEBUG", "spring.main.allow-bean-definition-overriding=true"})
public class DetailedLogAspectTest {

    @Autowired
    private TestController testController;

    @Test
    void whenMethodCalled_thenDetailedLogIsGenerated(CapturedOutput output) {
        testController.testMethod();
        String logOutput = output.toString();
        assertTrue(logOutput.contains("Operation Starting"),
                "Log should contain operation start message. Actual log: " + logOutput);
    }

    @LogDetails
    void testMethod() {
        System.out.println("Test method called");
    }
}
