package io.github.spring.logger.aspect;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ContextConfiguration;
import io.github.spring.logger.config.TestConfig;
import io.github.spring.logger.controller.TestController;

@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class, TestController.class})
@ExtendWith(OutputCaptureExtension.class)
public class DetailedLogAspectTest {

    @Autowired
    private TestController testController;

    @Test
    public void whenMethodCalled_thenDetailedLogIsGenerated(CapturedOutput output) {
        testController.testMethod();

        String logs = output.getOut();
        assertTrue(logs.contains("Method: TestController.testMethod"));
        assertTrue(logs.contains("🚀 Operation Starting"));
        assertTrue(logs.contains("✅ Operation Completed"));
        assertTrue(logs.contains("\"Test successful!\""));
    }
}
