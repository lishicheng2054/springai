package com.example.interviewbeginner.modules.llmprovider.dto;

/**
 * Provider 连通性测试结果。
 */
public record ProviderTestResult(
        boolean success,
        String message,
        String model
) {
}
