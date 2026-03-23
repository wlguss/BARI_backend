package com.bari.inventory.dto.request;

import lombok.Getter;

@Getter
public class InventoryKafkaRequest {
    private Long inventoryId;
    private Integer quantity;
}
