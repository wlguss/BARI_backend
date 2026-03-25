package com.bari.order.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * discount-service 내부 API 응답 매핑용 DTO.
 */
@Getter
@NoArgsConstructor
public class DiscountInfo {
    private Long id;
    private Long inventoryId;
    private Integer originalPrice;
    private Integer discountPrice;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
