package com.bari.inventory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * product-service Feign 응답 DTO.
 * ProductResponseDTO의 필드를 매핑합니다.
 */
@Getter
@NoArgsConstructor
public class StoreProductResponse {

    // product-service는 "id"로 반환
    @JsonProperty("id")
    private Long productId;

    private Long storeId;
    private String name;
    private String description;
    private String imageUrl;
}
