package com.bari.common.exception;

import com.bari.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리기.
 * libs:common에 위치하므로 각 서비스의 Application 클래스에
 * @SpringBootApplication(scanBasePackages = "com.bari") 설정이 필요합니다.
 */
@Slf4j
@RestControllerAdvice   
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리.
     * 서비스 레이어에서 throw new BusinessException(errorCode) 으로 던진 예외를 처리합니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("비즈니스 예외 발생: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    /**
     * @Valid 검증 실패 처리.
     * 요청 DTO의 유효성 검증 실패 시 발생하는 예외를 처리합니다.
     * 여러 필드의 에러 메시지를 하나의 문자열로 합쳐서 반환합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        // 모든 필드 에러 메시지를 ", "로 연결
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("유효성 검증 실패: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "VALIDATION_ERROR", message));
    }

    /**
     * 그 외 모든 예외 처리 (500 Internal Server Error).
     * 예상치 못한 예외가 발생했을 때의 fallback 처리입니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("예상치 못한 예외 발생: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "INTERNAL_SERVER_ERROR",
                        "서버 내부 오류가 발생했습니다."
                ));
    }
}
