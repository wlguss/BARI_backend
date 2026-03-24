package com.bari.discount.dto.response;

import java.time.LocalDateTime;

import com.bari.discount.dto.client.ProductInfo;
import com.bari.discount.entity.Discount;

import lombok.Builder;
import lombok.Getter;

/**
 * 매장 기준 할인 전체 목록 응답 DTO.
 * 상품 이미지 URL, 재고 ID, Discount 전체 필드를 포함합니다.
 */
@Getter
@Builder
public class StoreDiscountResponse {

    private Long discountId;
    private Long inventoryId;
    private Long productId;
    private String imageUrl;
    private Integer originalPrice;
    private Integer discountPrice;
    private Integer discountRate;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    public static StoreDiscountResponse of(Discount discount, ProductInfo product) {
        return StoreDiscountResponse.builder()
                .discountId(discount.getId())
                .inventoryId(discount.getInventoryId())
                .productId(product.getId())
                .imageUrl(product.getImageUrl())
                .originalPrice(discount.getOriginalPrice())
                .discountPrice(discount.getDiscountPrice())
                .discountRate(discount.getDiscountRate())
                .startAt(discount.getStartAt())
                .endAt(discount.getEndAt())
                .createdAt(discount.getCreatedAt())
                .build();
    }
}
