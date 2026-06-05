package com.example.interviewbeginner.modules.resume.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建简历请求。
 */
public record ResumeRequest(
        @NotBlank(message = "候选人姓名不能为空")
        @Size(max = 50, message = "姓名长度不能超过50")
        String candidateName,

        @NotBlank(message = "目标岗位不能为空")
        @Size(max = 100, message = "岗位名称长度不能超过100")
        String targetPosition,

        @NotBlank(message = "简历内容不能为空")
        String resumeText,

        @Size(max = 20)
        String sourceType
) {
}
