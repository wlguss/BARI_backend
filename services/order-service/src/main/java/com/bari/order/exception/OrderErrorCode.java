package com.bari.order.exception;

import com.bari.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * order-service 전용 에러 코드.
 */
@Getter // 선언된 필드의 getter 메서드를 자동으로 생성
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND(404, "주문을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "상품을 찾을 수 없습니다."),
    PRODUCT_SERVICE_UNAVAILABLE(503, "상품 서비스에 연결할 수 없습니다."),
    STORE_NOT_FOUND(404, "매장을 찾을 수 없습니다."),
    STORE_SERVICE_UNAVAILABLE(503, "매장 서비스에 연결할 수 없습니다."),
    ORDER_FORBIDDEN(403, "해당 주문에 접근 권한이 없습니다."),
    ORDER_ALREADY_CANCELLED(400, "이미 취소된 주문입니다."),
    ORDER_CANNOT_CANCEL(400, "취소할 수 없는 주문 상태입니다. (PENDING 또는 CONFIRMED 상태만 취소 가능)"),
    ORDER_CANNOT_UPDATE_STATUS(400, "변경할 수 없는 주문 상태입니다."),
    INVENTORY_NOT_ENOUGH(400, "재고가 부족합니다."),
    INVENTORY_SERVICE_UNAVAILABLE(503, "재고 서비스에 연결할 수 없습니다."),
    DISCOUNT_SERVICE_UNAVAILABLE(503, "할인 서비스에 연결할 수 없습니다.");

    private final int status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();  // enum의 이름을 코드로 사용  
    }
}
