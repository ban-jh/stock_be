package org.shaloman.pj.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 모든 REST API가 공유할 응답 형식입니다.
 * success/data/error 필드로 성공과 실패를 분리해 프론트엔드 처리를 단순화합니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, T data, ApiError error) {
    /** 정상 결과를 감싸는 응답을 생성합니다. */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /** 내부 예외정보를 노출하지 않는 오류 응답을 생성합니다. */
    public static <T> ApiResponse<T> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message));
    }

    public record ApiError(String code, String message) {}
}
