package com.bari.user.exception;

import com.bari.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * user-service 전용 추가 예외 처리기.
 * libs/common의 GlobalExceptionHandler에서 처리하지 않는 예외를 추가로 처리합니다.
 */
@RestControllerAdvice
public class UserExceptionHandler {

    /**
     * 필수 요청 헤더 누락 처리 (예: Authorization 헤더 없이 호출 시)
     * → 401 Unauthorized 반환
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(
                        HttpStatus.UNAUTHORIZED.value(),
                        "MISSING_HEADER",
                        e.getHeaderName() + " 헤더가 필요합니다."
                ));
    }
}
