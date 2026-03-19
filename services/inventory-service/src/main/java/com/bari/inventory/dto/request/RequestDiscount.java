package com.bari.inventory.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RequestDiscount {
    private Long inventoryId;
    private Integer originalPrice;
    private Integer discountRate;
}