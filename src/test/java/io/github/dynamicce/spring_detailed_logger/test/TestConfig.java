package io.github.dynamicce.spring_detailed_logger.test;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@EnableAutoConfiguration
@ComponentScan(basePackages = {"io.github.dynamicce.spring_detailed_logger.aspect",
        "io.github.dynamicce.spring_detailed_logger.test"})
public class TestConfig {
}
