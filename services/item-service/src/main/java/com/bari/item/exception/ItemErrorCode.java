package com.bari.item.exception;

import com.bari.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 아이템 서비스 에러 코드.
 * ErrorCode 인터페이스를 구현하는 enum입니다.
 */
@Getter
@RequiredArgsConstructor
public enum ItemErrorCode implements ErrorCode {

    /** 아이템을 찾을 수 없음 (404) */
    ITEM_NOT_FOUND(404, "ITEM_NOT_FOUND", "아이템을 찾을 수 없습니다."),

    /** 접근 권한 없음 (403) — 본인이 생성하지 않은 아이템 수정/삭제 시도 */
    ITEM_FORBIDDEN(403, "ITEM_FORBIDDEN", "접근 권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
