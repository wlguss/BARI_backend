package com.bari.inventory.exception;

import com.bari.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InventoryErrorCode implements ErrorCode {

    // =========================
    // 400 BAD REQUEST
    // =========================
    INVALID_QUANTITY(400, "INVALID_QUANTITY", "수량은 0 이상이어야 합니다."),
    INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "잘못된 요청 값입니다."),

    // =========================
    // 404 NOT FOUND
    // =========================
    INVENTORY_NOT_FOUND(404, "INVENTORY_NOT_FOUND", "재고를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "상품이 존재하지 않습니다."),
    PRODUCT_DELETED(404, "PRODUCT_DELETED", "삭제된 상품입니다."),
    INVENTORY_DELETED(404, "INVENTORY_DELETED", "삭제된 재고입니다."),

    // =========================
    // 409 CONFLICT
    // =========================
    INVENTORY_ALREADY_DELETED(409, "INVENTORY_ALREADY_DELETED", "이미 삭제된 재고입니다."),
    INSUFFICIENT_STOCK(409, "INSUFFICIENT_STOCK", "재고가 부족합니다."),

    // =========================
    // 500 INTERNAL SERVER ERROR
    // =========================
    INVENTORY_UPDATE_FAILED(500, "INVENTORY_UPDATE_FAILED", "재고 수정 중 오류가 발생했습니다."),
    INVENTORY_CREATE_FAILED(500, "INVENTORY_CREATE_FAILED", "재고 생성 중 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;
}
