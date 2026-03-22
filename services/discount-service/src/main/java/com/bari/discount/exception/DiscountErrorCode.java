package com.bari.discount.exception;

import com.bari.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountErrorCode implements ErrorCode {

    // =========================
    // 400 BAD REQUEST
    // =========================
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "잘못된 요청 값입니다."),
    INVALID_DISCOUNT_VALUE(400, "INVALID_DISCOUNT_VALUE", "할인 값은 0 이상이어야 합니다."),
    INVALID_DATE_RANGE(400, "INVALID_DATE_RANGE", "시작일은 종료일보다 이전이어야 합니다."),

    // =========================
    // 404 NOT FOUND
    // =========================
    DISCOUNT_NOT_FOUND(404, "DISCOUNT_NOT_FOUND", "할인을 찾을 수 없습니다."),
    INVENTORY_NOT_FOUND(404, "INVENTORY_NOT_FOUND", "재고가 존재하지 않습니다."),
    INVENTORY_DELETED(404, "INVENTORY_DELETED", "삭제된 재고입니다."),
    DISCOUNT_DELETED(404, "DISCOUNT_DELETED", "삭제된 할인입니다."),

    // =========================
    // 409 CONFLICT
    // =========================
    DISCOUNT_ALREADY_DELETED(409, "DISCOUNT_ALREADY_DELETED", "이미 삭제된 할인입니다."),

    // =========================
    // 500
    // =========================
    DISCOUNT_CREATE_FAILED(500, "DISCOUNT_CREATE_FAILED", "할인 생성 중 오류가 발생했습니다."),
    DISCOUNT_UPDATE_FAILED(500, "DISCOUNT_UPDATE_FAILED", "할인 수정 중 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
