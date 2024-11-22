package io.github.dynamicce.logger.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "io.github.dynamicce.logger")
public class TestConfig {

    @Bean
    public Environment environment() {
        return new StandardEnvironment();
    }
}
