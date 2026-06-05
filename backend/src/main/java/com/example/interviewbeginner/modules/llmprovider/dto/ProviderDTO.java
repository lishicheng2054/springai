package com.example.interviewbeginner.modules.llmprovider.dto;

import java.time.LocalDateTime;

/**
 * Provider 响应体。
 */
public record ProviderDTO(
        String id,
        String baseUrl,
        String maskedApiKey,
        String model,
        Double temperature,
        boolean enabled,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
