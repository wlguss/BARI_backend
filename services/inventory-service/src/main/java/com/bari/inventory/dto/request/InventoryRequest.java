package com.bari.inventory.dto.request;

import java.time.LocalDateTime;

import com.bari.inventory.entity.Inventory;

import lombok.Getter;

@Getter
public class InventoryRequest {
    private Long productId;
    private Integer quantity;
    private LocalDateTime expireAt;

    // discount
    private Integer originalPrice;
    private Integer discountPrice;

    public Inventory toEntity() {
        return Inventory.builder()
                .productId(productId)
                .quantity(quantity)
                .expireAt(expireAt)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
