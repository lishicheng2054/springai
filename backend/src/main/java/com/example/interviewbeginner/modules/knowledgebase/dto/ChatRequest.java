package com.example.interviewbeginner.modules.knowledgebase.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * RAG 问答请求。
 */
public record ChatRequest(
        @NotBlank(message = "问题不能为空")
        String question
) {
}
