package com.bari.product.exception;

import com.bari.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter    // 추상 메서드 getStatus(), getCode(), getMessage()를 구현한 enum에 대해 자동으로 getter 메서드를 생성합니다.
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {

    PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    PRODUCT_ALREADY_DELETED(400, "PRODUCT_ALREADY_DELETED", "이미 삭제된 상품입니다."),
    PRODUCT_ACCESS_DENIED(403, "PRODUCT_ACCESS_DENIED", "상품에 대한 권한이 없습니다."),
    INVALID_PRODUCT_ROLE(403, "INVALID_PRODUCT_ROLE", "상품 관리 권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}