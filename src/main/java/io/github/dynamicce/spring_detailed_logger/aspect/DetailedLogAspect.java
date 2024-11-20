package io.github.dynamicce.spring_detailed_logger.aspect;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DetailedLogAspect {
    private static final Logger logger = LoggerFactory.getLogger("operation-logs");
    private static final String CORRELATION_ID = "correlationId";
    private static final long SLOW_EXECUTION_THRESHOLD = 1000L;
    private static final String ANONYMOUS_USER = "anonymous";
    private static final String DEFAULT_PROFILE = "default";
    private static final Logger operationsLogger = LoggerFactory.getLogger("operations");

    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Around("@annotation(io.github.dynamicce.spring_detailed_logger.annotation.LogDetails)")
    public Object logMethodDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        long startTime = System.currentTimeMillis();
        String correlationId = generateCorrelationId();

        MDC.put(CORRELATION_ID, correlationId);

        try {
            logStartOperation(joinPoint, methodName, correlationId);
            Object result = joinPoint.proceed();
            logEndOperation(methodName, startTime, result, correlationId);
            operationsLogger.info("Important Operation: {}", methodName);
            return result;
        } catch (IOException | IllegalArgumentException e) {
            logError(methodName, e, correlationId);
            throw e;
        } catch (RuntimeException e) {
            logError(methodName, e, correlationId);
            throw e;
        } finally {
            MDC.remove(CORRELATION_ID);
        }
    }

    private void logStartOperation(ProceedingJoinPoint joinPoint, String methodName,
            String correlationId) {
        try {
            OperationContext context = buildOperationContext();
            logger.warn("""
                    🚀 Operation Starting
                    Method: {}
                    User: {}
                    IP: {}
                    Headers: {}
                    Memory: {} MB
                    Environment: {}
                    CorrelationId: {}
                    Parameters: {}""", methodName, context.username(), context.clientIP(),
                    context.headers(), context.usedMemory(), context.activeProfile(), correlationId,
                    objectMapper.writeValueAsString(joinPoint.getArgs()));
        } catch (Exception e) {
            logger.error("Error while logging start operation", e);
        }
    }

    private void logEndOperation(String methodName, long startTime, Object result,
            String correlationId) {
        try {
            long duration = System.currentTimeMillis() - startTime;
            String performanceWarning = duration > SLOW_EXECUTION_THRESHOLD ? "⚠️ SLOW OPERATION!"
                    : "✅ Normal duration";

            logger.warn("""
                    ✅ Operation Completed
                    Method: {}
                    Duration: {} ms {}
                    Result: {}
                    CorrelationId: {}""", methodName, duration, performanceWarning,
                    objectMapper.writeValueAsString(result), correlationId);
        } catch (Exception e) {
            logger.error("Error while logging end operation", e);
        }
    }

    private void logError(String methodName, Exception e, String correlationId) {
        logger.error("""
                ❌ Error Occurred
                Method: {}
                Error: {}
                Time: {}
                Stack: {}
                CorrelationId: {}""", methodName, e.getMessage(), LocalDateTime.now(),
                e.getStackTrace(), correlationId);
    }

    private OperationContext buildOperationContext() throws Exception {
        HttpServletRequest request = getCurrentRequest();
        String username = ANONYMOUS_USER;
        String clientIP = request != null ? request.getRemoteAddr() : "N/A";
        Map<String, String> headers = collectHeaders(request);
        long usedMemory = calculateUsedMemory();
        String activeProfile = determineActiveProfile();

        return new OperationContext(username, clientIP, objectMapper.writeValueAsString(headers),
                usedMemory, activeProfile);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private Map<String, String> collectHeaders(HttpServletRequest request) {
        if (request == null) {
            return Collections.emptyMap();
        }
        return Collections.list(request.getHeaderNames()).stream().collect(Collectors.toMap(
                headerName -> headerName, request::getHeader, (existing, replacement) -> existing));
    }

    private long calculateUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
    }

    private String determineActiveProfile() {
        return environment.getActiveProfiles().length > 0 ? environment.getActiveProfiles()[0]
                : DEFAULT_PROFILE;
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}


record OperationContext(String username, String clientIP, String headers, long usedMemory,
        String activeProfile) {
}
