package com.bari.store.exception;

import com.bari.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreErrorCode implements ErrorCode {

    STORE_NOT_FOUND(404, "STORE_NOT_FOUND", "매장을 찾을 수 없습니다."),
    DISCOUNT_SERVICE_UNAVAILABLE(503, "DISCOUNT_SERVICE_UNAVAILABLE", "할인 서비스에 연결할 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
