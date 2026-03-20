package com.bari.order.event;

import com.bari.order.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 취소 이벤트.
 * inventory-service가 이 이벤트를 수신하여 재고를 복구합니다.
 *
 * Kafka 토픽: order.cancelled
 */
@Getter
@Builder
public class OrderCancelledEvent {

    private Long orderId;
    private Long customerId;
    private Long storeId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime occurredAt;

    public static OrderCancelledEvent from(Order order) {
        return OrderCancelledEvent.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
