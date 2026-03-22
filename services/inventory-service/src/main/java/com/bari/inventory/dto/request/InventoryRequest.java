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
    private Integer price;
    private String memo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expireAt;

    public Inventory toEntity() {
        return Inventory.builder()
                .productId(productId)
                .quantity(quantity)
                .price(price)
                .expireAt(expireAt)
                .memo(memo)
                .build();
    }
}
