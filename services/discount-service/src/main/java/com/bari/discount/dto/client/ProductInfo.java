package com.bari.discount.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * product-service 내부 API 응답 매핑용 DTO.
 */
@Getter
@NoArgsConstructor
public class ProductInfo {
    private Long id;
    private Long storeId;
    private String name;
}
