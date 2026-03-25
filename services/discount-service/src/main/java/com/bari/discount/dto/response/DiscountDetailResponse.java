package com.bari.discount.dto.response;

import com.bari.discount.dto.client.InventoryInfo;
import com.bari.discount.dto.client.ProductInfo;
import com.bari.discount.dto.client.StoreInfo;
import com.bari.discount.entity.Discount;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 고객용 할인 상품 상세 응답 DTO.
 * 할인 정보 + 재고 정보 + 상품 정보 + 매장 정보를 모두 포함합니다.
 */
@Getter
@Builder
public class DiscountDetailResponse {

    // 할인 정보
    private Long discountId;
    private Integer originalPrice;
    private Integer discountPrice;
    private Integer discountRate;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    // 재고 정보
    private Long inventoryId;
    private Integer quantity;
    private Integer inventoryPrice;
    private LocalDateTime expireAt;
    private String memo;

    // 상품 정보
    private Long productId;
    private String productName;
    private String productDescription;
    private String productImageUrl;

    // 매장 정보
    private Long storeId;
    private String storeName;
    private String storeDescription;
    private String storeAddress;
    private String storePhone;
    private String businessHours;
    private String category;
    private String storeImageUrl;

    public static DiscountDetailResponse of(Discount discount, InventoryInfo inventory,
                                            ProductInfo product, StoreInfo store) {
        return DiscountDetailResponse.builder()
                // 할인
                .discountId(discount.getId())
                .originalPrice(discount.getOriginalPrice())
                .discountPrice(discount.getDiscountPrice())
                .discountRate(discount.getDiscountRate())
                .startAt(discount.getStartAt())
                .endAt(discount.getEndAt())
                .createdAt(discount.getCreatedAt())
                // 재고
                .inventoryId(inventory.getId())
                .quantity(inventory.getQuantity())
                .inventoryPrice(inventory.getPrice())
                .expireAt(inventory.getExpireAt())
                .memo(inventory.getMemo())
                // 상품
                .productId(product.getId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .productImageUrl(product.getImageUrl())
                // 매장
                .storeId(store != null ? store.getId() : null)
                .storeName(store != null ? store.getStoreName() : null)
                .storeDescription(store != null ? store.getDescription() : null)
                .storeAddress(store != null ? store.getAddress() : null)
                .storePhone(store != null ? store.getPhone() : null)
                .businessHours(store != null ? store.getBusinessHours() : null)
                .category(store != null ? store.getCategory() : null)
                .storeImageUrl(store != null ? store.getImageUrl() : null)
                .build();
    }
}
