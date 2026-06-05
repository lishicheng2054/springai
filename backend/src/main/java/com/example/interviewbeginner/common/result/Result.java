package com.example.interviewbeginner.common.result;

import lombok.Getter;

/**
 * 统一响应体。
 * <p>
 * 遵循 API 契约文档：成功时 code=0，失败时 code 为对应错误码。
 * HTTP 状态码始终为 200，通过业务 code 区分成功/失败。
 */
@Getter
public class Result<T> {

    private final int code;
    private final String message;
    private final T data;

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // ========== 成功 ==========

    public static <T> Result<T> success() {
        return new Result<>(0, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(0, "success", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(0, message, data);
    }

    // ========== 失败 ==========

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(int code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public boolean isSuccess() {
        return this.code == 0;
    }
}
