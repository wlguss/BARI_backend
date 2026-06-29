package com.bari.user.exception;

import com.bari.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 서비스 에러 코드.
 * ErrorCode 인터페이스를 구현하는 enum입니다.
 *
 * GlobalExceptionHandler에서 BusinessException 발생 시
 * 이 enum의 값을 기반으로 에러 응답을 생성합니다.
 */
@Getter  // 모든 필드에 getXxx() 메서드 자동 생성
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    /** 사용자를 찾을 수 없음 (404) */
    USER_NOT_FOUND(404, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),

    /** 이메일 중복 (409) */
    EMAIL_DUPLICATED(409, "EMAIL_DUPLICATED", "이미 사용 중인 이메일입니다."),

    /** 비밀번호 불일치 (401) */
    INVALID_PASSWORD(401, "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),

    /** 유효하지 않은 토큰 (401) */
    INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");

    private final int status;
    private final String code;
    private final String message;
}
