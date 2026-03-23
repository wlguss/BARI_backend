package com.bari.discount.dto.response;

import com.bari.discount.dto.client.ProductInfo;
import com.bari.discount.entity.Discount;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 찜한 매장의 할인 임박 상품 응답 DTO.
 * store-service의 홈화면 노출용으로 사용됩니다.
 */
@Getter
@Builder
public class ExpiringDiscountResponse {
    private Long discountId;
    private Long inventoryId;
    private Long productId;
    private String productName;
    private Long storeId;
    private Integer discountPrice;
    private Integer discountRate;
    private LocalDateTime endAt;

    public static ExpiringDiscountResponse of(Discount discount, ProductInfo product) {
        return ExpiringDiscountResponse.builder()
                .discountId(discount.getId())
                .inventoryId(discount.getInventoryId())
                .productId(product.getId())
                .productName(product.getName())
                .storeId(product.getStoreId())
                .discountPrice(discount.getDiscountPrice())
                .discountRate(discount.getDiscountRate())
                .endAt(discount.getEndAt())
                .build();
    }
}
