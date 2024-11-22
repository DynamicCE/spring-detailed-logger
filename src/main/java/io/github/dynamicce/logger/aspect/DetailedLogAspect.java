package io.github.dynamicce.logger.aspect;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.dynamicce.logger.model.OperationContext;
import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class DetailedLogAspect {
    private static final Logger logger = LoggerFactory.getLogger("DETAILED_OPERATIONS");
    private static final String CORRELATION_ID = "correlationId";
    private static final long SLOW_EXECUTION_THRESHOLD = 1000L;
    private static final String ANONYMOUS_USER = "anonymous";
    private static final String DEFAULT_PROFILE = "default";

    private final ObjectMapper objectMapper;
    private final Environment environment;

    @Autowired
    public DetailedLogAspect(ObjectMapper objectMapper, Environment environment) {
        this.objectMapper = objectMapper;
        this.environment = environment;
    }

    @Around("@annotation(io.github.dynamicce.logger.annotation.LogDetails)")
    public Object logMethodDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "." + signature.getName();
        long startTime = System.currentTimeMillis();
        String correlationId = UUID.randomUUID().toString();

        MDC.put(CORRELATION_ID, correlationId);

        try {
            logStartOperation(joinPoint, methodName, correlationId);
            Object result = joinPoint.proceed();
            logEndOperation(methodName, startTime, result, correlationId);
            return result;
        } catch (Exception e) {
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

            logger.info("""
                    🚀 Operation Starting
                    Method: {}
                    User: {}
                    Client IP: {}
                    Headers: {}
                    Memory Usage: {} MB
                    Active Profile: {}
                    Correlation ID: {}
                    Parameters: {}""", methodName, context.username(), context.clientIP(),
                    context.headers(), context.usedMemory(), context.activeProfile(), correlationId,
                    objectMapper.writeValueAsString(joinPoint.getArgs()));
        } catch (JsonProcessingException | IllegalArgumentException e) {
            logger.error("Error occurred while logging operation start", e);
        }
    }

    private void logEndOperation(String methodName, long startTime, Object result,
            String correlationId) {
        try {
            long duration = System.currentTimeMillis() - startTime;
            String performanceWarning = duration > SLOW_EXECUTION_THRESHOLD ? "⚠️ SLOW OPERATION!"
                    : "✅ Normal duration";

            logger.info("""
                    ✅ Operation Completed
                    Method: {}
                    Duration: {} ms {}
                    Result: {}
                    Correlation ID: {}""", methodName, duration, performanceWarning,
                    objectMapper.writeValueAsString(result), correlationId);
        } catch (JsonProcessingException | IllegalArgumentException e) {
            logger.error("Error occurred while logging operation end", e);
        }
    }

    private void logError(String methodName, Exception e, String correlationId) {
        logger.error("""
                ❌ Operation Failed
                Method: {}
                Error Message: {}
                Error Type: {}
                Correlation ID: {}
                Stack Trace: {}""", methodName, e.getMessage(), e.getClass().getSimpleName(),
                correlationId, e.getStackTrace());
    }

    private OperationContext buildOperationContext() {
        HttpServletRequest request = getCurrentRequest();
        String username = ANONYMOUS_USER;
        String clientIP = "Unknown";
        Map<String, String> headers = Collections.emptyMap();

        if (request != null) {
            clientIP = extractClientIP(request);
            headers = collectHeaders(request);
        }

        return new OperationContext(username, clientIP, headers, calculateUsedMemory(),
                determineActiveProfile());
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            logger.debug("Could not get current request", e);
            return null;
        }
    }

    private String extractClientIP(HttpServletRequest request) {
        String[] headerNames = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR" };

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0];
            }
        }

        return request.getRemoteAddr();
    }

    private Map<String, String> collectHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        return headers;
    }

    private long calculateUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
    }

    private String determineActiveProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return activeProfiles.length > 0 ? activeProfiles[0] : DEFAULT_PROFILE;
    }
}
