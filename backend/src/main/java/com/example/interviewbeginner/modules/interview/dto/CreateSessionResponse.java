package com.example.interviewbeginner.modules.interview.dto;

/**
 * 创建面试会话响应。
 */
public record CreateSessionResponse(
        String sessionId,
        FirstQuestion firstQuestion,
        int currentStep,
        int totalStep
) {

    public record FirstQuestion(
            Long questionId,
            String content
    ) {
    }
}
