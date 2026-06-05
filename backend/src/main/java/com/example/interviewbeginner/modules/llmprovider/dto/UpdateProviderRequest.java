package com.example.interviewbeginner.modules.llmprovider.dto;

/**
 * 更新 Provider 请求（所有字段可选）。
 */
public record UpdateProviderRequest(
        String baseUrl,
        String apiKey,
        String model,
        Double temperature,
        Boolean enabled
) {
}
