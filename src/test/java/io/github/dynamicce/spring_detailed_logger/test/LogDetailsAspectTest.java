package io.github.dynamicce.spring_detailed_logger.test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = {TestConfig.class, TestController.class})
@ExtendWith(OutputCaptureExtension.class)
public class LogDetailsAspectTest {

    @Autowired
    private TestController testController;

    @Test
    public void whenMethodCalled_thenDetailedLogIsGenerated(CapturedOutput output) {
        // when
        testController.testMethod();

        // then
        String logs = output.getOut();
        assertTrue(logs.contains("Method: testMethod"), "Log should contain method name");
        assertTrue(logs.contains("Operation Starting"),
                "Log should contain operation start message");
        assertTrue(logs.contains("Operation Completed"),
                "Log should contain operation complete message");
        assertTrue(logs.contains("Result: \"Test successful!\""),
                "Log should contain return value");
    }
}
