package com.bari.discount.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * inventory-service 내부 API 응답 매핑용 DTO.
 */
@Getter
@NoArgsConstructor
public class InventoryInfo {
    private Long id;
    private Long productId;
}
