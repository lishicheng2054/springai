package com.example.interviewbeginner.modules.interview.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建面试会话请求。
 */
public record CreateSessionRequest(
        @NotNull(message = "简历ID不能为空")
        Long resumeId,

        @NotBlank(message = "岗位类型不能为空")
        String roleType,

        @Min(value = 1, message = "题目数量至少为1")
        int questionCount
) {
}
