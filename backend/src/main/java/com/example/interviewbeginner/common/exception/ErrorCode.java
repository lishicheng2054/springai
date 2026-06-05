package com.example.interviewbeginner.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误码枚举，按模块分段。
 * <ul>
 *   <li>1xxx - 通用错误</li>
 *   <li>2xxx - 简历模块</li>
 *   <li>3xxx - 面试模块</li>
 *   <li>7xxx - AI 服务</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ========== 通用 (1xxx) ==========
    BAD_REQUEST(40001, "参数错误"),
    NOT_FOUND(40004, "资源不存在"),
    BUSINESS_ERROR(40009, "业务状态不允许"),
    INTERNAL_ERROR(50000, "系统内部错误"),

    // ========== 简历 (2xxx) ==========
    RESUME_NOT_FOUND(20001, "简历不存在"),

    // ========== 面试 (3xxx) ==========
    INTERVIEW_SESSION_NOT_FOUND(30001, "面试会话不存在"),
    INTERVIEW_ALREADY_COMPLETED(30002, "面试已结束"),
    INTERVIEW_QUESTION_NOT_FOUND(30003, "面试题目不存在"),
    INTERVIEW_EVALUATION_FAILED(30005, "面试评估失败"),
    INTERVIEW_QUESTION_GENERATION_FAILED(30006, "面试题目生成失败"),

    // ========== AI 服务 (7xxx) ==========
    AI_SERVICE_ERROR(70003, "AI 服务调用失败");

    private final int code;
    private final String message;
}
