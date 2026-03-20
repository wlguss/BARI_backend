package com.bari.order.event;

import com.bari.order.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 픽업 예약 완료 이벤트.
 * inventory-service가 이 이벤트를 수신하여 재고를 차감합니다.
 *
 * Kafka 토픽: order.reserved
 */
@Getter
@Builder
public class OrderReservedEvent {

    private Long orderId;
    private Long customerId;
    private Long storeId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime pickupTime;
    private LocalDateTime occurredAt;

    public static OrderReservedEvent from(Order order) {
        return OrderReservedEvent.builder()
                .orderId(order.getId())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .pickupTime(order.getPickupTime())
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
