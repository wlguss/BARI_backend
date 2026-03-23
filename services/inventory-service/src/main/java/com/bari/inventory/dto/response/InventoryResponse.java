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

    private Integer price;
    private LocalDateTime expireAt;
    private String memo;

    public static InventoryResponse fromEntity(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .price(inventory.getPrice())
                .expireAt(inventory.getExpireAt())
                .memo(inventory.getMemo())
                .build();
    }
}
