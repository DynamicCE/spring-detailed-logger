package io.github.dynamicce.logger.model;

import java.util.Map;

public record OperationContext(String username, String clientIP, Map<String, String> headers,
        long usedMemory, String activeProfile) {
}
