package com.bari.order.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * inventory-service 재고 조회 응답 DTO.
 * inventory-service: GET /api/store/inventory/product/{productId} 응답 매핑용
 */
@Getter
@NoArgsConstructor
public class InventoryInfo {

    private Long id;
    private Long productId;
    private Integer quantity;
    private Integer price;
    private LocalDateTime expireAt;
}
