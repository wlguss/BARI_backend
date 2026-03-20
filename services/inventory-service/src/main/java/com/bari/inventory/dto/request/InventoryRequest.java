package com.bari.inventory.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.bari.inventory.entity.Inventory;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class InventoryRequest {
    private Long productId;
    private Integer quantity;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expireAt;

    private Integer originalPrice;

    public Inventory toEntity() {
        return Inventory.builder()
                .productId(productId)
                .quantity(quantity)
                .expireAt(expireAt)
                .build();
    }
}
