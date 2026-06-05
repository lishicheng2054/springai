package com.example.interviewbeginner.modules.interview.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 提交答案请求。
 */
public record SubmitAnswerRequest(
        @NotNull(message = "题目ID不能为空")
        Long questionId,

        @NotBlank(message = "答案内容不能为空")
        String answerText
) {
}
