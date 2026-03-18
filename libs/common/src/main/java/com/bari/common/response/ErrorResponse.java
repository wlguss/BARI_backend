package com.bari.common.response;

import com.bari.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 에러 응답 DTO.
 * GlobalExceptionHandler에서 예외 발생 시 이 형식으로 응답합니다.
 *
 * 예시:
 * {
 *   "status": 404,
 *   "code": "USER_NOT_FOUND",
 *   "message": "사용자를 찾을 수 없습니다."
 * }
 */
@Getter
public class ErrorResponse {

    /** HTTP 상태 코드 */
    private final int status;

    /** 에러 코드 식별자 */
    private final String code;

    /** 에러 메시지 */
    private final String message;

    private ErrorResponse(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    /**
     * ErrorCode로부터 ErrorResponse 생성
     */
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 직접 값을 지정해서 ErrorResponse 생성 (500 Internal Server Error 등)
     */
    public static ErrorResponse of(int status, String code, String message) {
        return new ErrorResponse(status, code, message);
    }
}
