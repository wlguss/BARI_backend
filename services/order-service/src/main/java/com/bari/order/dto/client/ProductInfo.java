package com.bari.order.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * product-service 내부 API 응답 DTO.
 * GET /api/internal/products/{productId} 응답을 역직렬화합니다.
 */
@Getter
@NoArgsConstructor
public class ProductInfo {

    private Long id;
    private Long storeId;
    private String name;
    private String description;
    private String imageUrl;
}
