package com.bari.order.entity;

/**
 * 주문 상태 열거형.
 *
 * 상태 전이 흐름:
 * PENDING(대기) → CONFIRMED(확정) → READY(픽업 준비 완료) → COMPLETED(완료)
 *                                                           ↘ CANCELLED(취소)
 */
public enum OrderStatus {

    /** 주문 접수 대기 중 (고객이 예약한 직후) */
    PENDING,

    /** 매장이 주문 확정 */
    CONFIRMED,

    /** 픽업 준비 완료 */
    READY,

    /** 픽업 완료 */
    COMPLETED,

    /** 주문 취소 (고객 취소 또는 매장 취소) */
    CANCELLED
}
