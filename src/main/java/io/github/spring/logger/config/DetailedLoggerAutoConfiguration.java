package io.github.spring.logger.config;

import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.spring.logger.aspect.DetailedLogAspect;

@Configuration
@EnableAspectJAutoProxy
@ConditionalOnClass({Aspect.class, LoggerFactory.class})
@ConditionalOnProperty(prefix = "detailed.logger", name = "enabled", havingValue = "true",
        matchIfMissing = true)
public class DetailedLoggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public DetailedLogAspect detailedLogAspect(ObjectMapper objectMapper, Environment environment) {
        return new DetailedLogAspect(objectMapper, environment);
    }
}
