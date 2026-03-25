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
    private String storeName;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Integer productPrice;
    private Integer price;
    private OrderStatus status;
    private LocalDateTime pickupTime;
    private LocalDateTime createdAt;

    /** Entity → DTO */
    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .productId(order.getProductId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .productPrice(order.getPrice() != null && order.getQuantity() != null && order.getQuantity() > 0
                        ? order.getPrice() / order.getQuantity() : null)
                .price(order.getPrice())
                .status(order.getStatus())
                .pickupTime(order.getPickupTime())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
