package com.bootstrap.backend.common.exception;

public enum ErrorCode {
    // 인증 관련 에러
    UNAUTHORIZED(401, "Unauthorized"),
    INVALID_TOKEN(401, "Invalid token"),

    // 사용자 관련 에러
    USER_NOT_FOUND(404, "User not found"),
    DUPLICATE_USER(400, "Duplicate user"),

    // 서버 관련 에러
    INTERNAL_SERVER_ERROR(500, "Internal server error"),

    // 요청 관련 에러
    BAD_REQUEST(400, "Bad request"),
    INVALID_REQUEST(400, "Invalid request"),

    // 기타
    NOT_FOUND(404, "Not found"),
    FORBIDDEN(403, "Forbidden");

    private final int status;
    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
