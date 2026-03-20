package com.bari.product.dto.response;

import java.time.LocalDateTime;


import com.bari.product.entity.ProductEntity;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDTO {

    private Long id;
    private Long storeId;
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public static ProductResponseDTO from(ProductEntity product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .storeId(product.getStoreId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .createdAt(product.getCreatedAt())
                .deletedAt(product.getDeletedAt())
                .build();
    }
}