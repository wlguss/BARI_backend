package com.bari.discount.dto.request;

import java.time.LocalDateTime;

import com.bari.discount.entity.Discount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountRequest {

    private Long inventoryId;
    private Integer originalPrice;
    private Integer discountPrice;
    private Integer discountRate;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public Discount toEntity() {

        return Discount.builder()
                .inventoryId(inventoryId)
                .originalPrice(originalPrice)
                .discountPrice(discountPrice)
                .discountRate(discountRate)
                .startAt(startAt != null ? startAt : LocalDateTime.now())
                .endAt(endAt)
                .build();
    }
}