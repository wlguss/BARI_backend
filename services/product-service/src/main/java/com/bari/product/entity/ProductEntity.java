package com.bari.product.entity;

import com.bari.common.entity.BaseTimeEntity;
import com.bari.product.dto.request.ProductRequestDTO;
import com.bari.product.dto.request.ProductUpdateDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Builder
    private ProductEntity(Long storeId, String name, String description, String imageUrl) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public static ProductEntity create(ProductRequestDTO request) {
        return ProductEntity.builder()
                .storeId(request.getStoreId())
                .name(request.getName())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .build();
    }

    public void update(ProductUpdateDTO request) {
        if (request.getName() != null && !request.getName().isBlank()) {
            this.name = request.getName();
        }
        if (request.getDescription() != null) {
            this.description = request.getDescription();
        }
        if (request.getImageUrl() != null) {
            this.imageUrl = request.getImageUrl();
        }
    }

    // 삭제시간 등록  
    public void softDelete() {
        this.softDelete();
    }
}