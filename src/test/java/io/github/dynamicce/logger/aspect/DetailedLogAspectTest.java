package io.github.dynamicce.logger.aspect;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import io.github.dynamicce.logger.config.TestConfig;
import io.github.dynamicce.logger.controller.TestController;

@SpringBootTest(classes = TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(OutputCaptureExtension.class)
public class DetailedLogAspectTest {

    @Autowired
    private TestController testController;

    @Test
    void whenMethodCalled_thenDetailedLogIsGenerated(CapturedOutput output) {
        // when
        testController.testMethod();

        // then
        assertTrue(output.getOut().contains("Operation Starting"));
        assertTrue(output.getOut().contains("Method: TestController.testMethod"));
        assertTrue(output.getOut().contains("Operation Completed"));
    }
}
