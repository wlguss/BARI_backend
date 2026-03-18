package com.bari.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API 공통 응답 래퍼 클래스.
 * 모든 API 응답은 이 형식으로 통일됩니다.
 *
 * 예시:
 * {
 *   "status": 200,
 *   "message": "성공",
 *   "data": { ... }   <- data가 null이면 JSON에서 제거됨
 * }
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** HTTP 상태 코드 */
    private final int status;

    /** 응답 메시지 */
    private final String message;

    /** 응답 데이터 (null이면 JSON에서 제거) */
    private final T data;

    private ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // ========== 정적 팩토리 메서드 ==========

    /**
     * 200 OK 성공 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "성공", data);
    }

    /**
     * 201 Created 성공 응답 (데이터 포함)
     */
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(), "생성 완료", data);
    }

    /**
     * 200 OK 성공 응답 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(HttpStatus.OK.value(), "성공", null);
    }

    /**
     * 에러 응답
     *
     * @param status  HTTP 상태
     * @param message 에러 메시지
     */
    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), message, null);
    }
}
