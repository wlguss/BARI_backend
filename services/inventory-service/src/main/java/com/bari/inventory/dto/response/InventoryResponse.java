package com.bari.inventory.dto.response;

import java.time.LocalDateTime;

import com.bari.inventory.entity.Inventory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InventoryResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
    private LocalDateTime expireAt;

    public static InventoryResponse fromEntity(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .expireAt(inventory.getExpireAt())
                .build();
    }
}
