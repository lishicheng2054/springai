package com.example.interviewbeginner.modules.knowledgebase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建知识库请求。
 */
public record CreateKbRequest(
        @NotBlank(message = "知识库名称不能为空")
        @Size(max = 200)
        String name,

        @Size(max = 1000)
        String description
) {
}
