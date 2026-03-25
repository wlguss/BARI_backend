package com.bari.store.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * discount-service 내부 API 응답 매핑용 DTO.
 * 찜한 매장의 할인 임박 상품 정보.
 */
@Getter
@NoArgsConstructor
public class ExpiringDiscountInfo {
    private Long discountId;
    private Long inventoryId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Long storeId;
    private Integer discountPrice;
    private Integer discountRate;
    private LocalDateTime endAt;
}
