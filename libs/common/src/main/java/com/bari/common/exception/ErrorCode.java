package com.bari.common.exception;

/**
 * 에러 코드 인터페이스.
 * 각 서비스의 에러 코드 enum이 이 인터페이스를 구현합니다.
 *
 * 사용 예시:
 * public enum UserErrorCode implements ErrorCode {
 *     USER_NOT_FOUND(404, "USER_NOT_FOUND", "사용자를 찾을 수 없습니다."),
 *     ...
 * }
 */
public interface ErrorCode {

    /**
     * HTTP 상태 코드 반환 (예: 400, 401, 403, 404, 409, 500)
     */
    int getStatus();

    /**
     * 에러 코드 식별자 반환 (예: "USER_NOT_FOUND")
     */
    String getCode();

    /**
     * 사용자에게 보여줄 에러 메시지 반환
     */
    String getMessage();
}
