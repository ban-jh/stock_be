package org.shaloman.pj.market.toss.exception;

/** 토스 API 오류를 상태와 내부 코드로 정규화합니다. 외부 응답 원문은 보관하지 않습니다. */
public class TossApiException extends RuntimeException {
    private final int httpStatus;
    private final String errorCode;
    private final String retryAfter;

    public TossApiException(int httpStatus, String errorCode, String message) {
        this(httpStatus, errorCode, message, null);
    }

    public TossApiException(int httpStatus, String errorCode, String message, String retryAfter) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.retryAfter = retryAfter;
    }

    public TossApiException(String errorCode, String message) {
        this(0, errorCode, message);
    }

    public TossApiException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = 0;
        this.errorCode = errorCode;
        this.retryAfter = null;
    }

    public int getHttpStatus() { return httpStatus; }
    public String getErrorCode() { return errorCode; }
    public String getRetryAfter() { return retryAfter; }
}
