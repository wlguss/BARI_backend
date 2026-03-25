package com.bari.inventory.dto.response;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * 매장 재고 전체 목록 응답 DTO.
 * 재고 정보 + 상품 정보를 함께 반환합니다.
 */
@Getter
@Builder
public class StoreInventoryResponse {

    // 재고 정보
    private Long inventoryId;
    private Integer quantity;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;

    // 상품 정보
    private Long productId;
    private String productName;
    private String productDescription;
    private String imageUrl;

    /**
     * native SQL Object[] 결과 매핑.
     * 컬럼 순서: [i.id, i.product_id, i.quantity, i.expire_at, i.created_at, p.name, p.description, p.image_url]
     */
    public static StoreInventoryResponse fromRow(Object[] row) {
        return StoreInventoryResponse.builder()
                .inventoryId(((Number) row[0]).longValue())
                .productId(((Number) row[1]).longValue())
                .quantity(row[2] != null ? ((Number) row[2]).intValue() : null)
                .expireAt(row[3] != null ? ((Timestamp) row[3]).toLocalDateTime() : null)
                .createdAt(row[4] != null ? ((Timestamp) row[4]).toLocalDateTime() : null)
                .productName((String) row[5])
                .productDescription((String) row[6])
                .imageUrl((String) row[7])
                .build();
    }
}
