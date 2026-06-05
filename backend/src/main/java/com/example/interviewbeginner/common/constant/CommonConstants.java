package com.example.interviewbeginner.common.constant;

/**
 * 通用常量，按内部类分组。
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /**
     * 业务状态码。
     */
    public static final class StatusCode {
        public static final int SUCCESS = 0;
        public static final int BAD_REQUEST = 40001;
        public static final int NOT_FOUND = 40004;
        public static final int BUSINESS_ERROR = 40009;
        public static final int SERVER_ERROR = 50000;

        private StatusCode() {
        }
    }

    /**
     * 分页默认值。
     */
    public static final class Pagination {
        public static final int DEFAULT_PAGE = 1;
        public static final int DEFAULT_SIZE = 20;
        public static final int MAX_SIZE = 100;

        private Pagination() {
        }
    }
}
