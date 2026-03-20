package com.bari.order.dto.response;

import com.bari.order.entity.Order;
import com.bari.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 주문 응답 DTO.
 */
@Getter
@Builder
public class OrderResponse {

    private Long id;
    private Long customerId;
    private Long storeId;
    private Long productId;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime pickupTime;
    private LocalDateTime createdAt;

    /** Entity → DTO */
    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .pickupTime(order.getPickupTime())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
