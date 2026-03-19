package com.bari.discount.dto.response;

import java.time.LocalDateTime;

import com.bari.discount.entity.Discount;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiscountResponse {

    private Long id;
    private Long inventoryId;
    private Integer discountRate;
    private Integer discountPrice;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static DiscountResponse from(Discount d) {
        return DiscountResponse.builder()
                .id(d.getId())
                .inventoryId(d.getInventoryId())
                .discountRate(d.getDiscountRate())
                .discountPrice(d.getDiscountPrice())
                .startAt(d.getStartAt())
                .endAt(d.getEndAt())
                .build();
    }
}
