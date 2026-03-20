package com.bari.inventory.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDiscount {
    private Long inventoryId;
    private Integer originalPrice;
    private Integer discountPrice;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}