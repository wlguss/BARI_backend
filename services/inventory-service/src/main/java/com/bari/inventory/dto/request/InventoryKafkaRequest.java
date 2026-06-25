package com.bari.inventory.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryKafkaRequest {
    private Long productId;
    private Integer quantity;
}
