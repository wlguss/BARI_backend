package com.bari.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 예외 클래스.
 * 서비스에서 비즈니스 규칙 위반 시 이 예외를 던집니다.
 *
 * 사용 예시:
 * throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 에러 코드 (상태 코드, 코드명, 메시지 포함) */
    private final ErrorCode errorCode;

    /**
     * ErrorCode를 받아 BusinessException 생성
     *
     * @param errorCode 에러 코드 enum 값
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode와 추가 메시지를 받아 BusinessException 생성
     *
     * @param errorCode 에러 코드 enum 값
     * @param message   추가 메시지
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
