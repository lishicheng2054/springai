package com.example.interviewbeginner.modules.llmprovider.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建 Provider 请求。
 */
public record CreateProviderRequest(
        @NotBlank(message = "Provider ID 不能为空")
        String id,

        @NotBlank(message = "baseUrl 不能为空")
        String baseUrl,

        @NotBlank(message = "API Key 不能为空")
        String apiKey,

        @NotBlank(message = "model 不能为空")
        String model,

        Double temperature
) {
}
