package com.bari.order.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * store-service 내부 API 응답 DTO.
 * GET /api/internal/stores/{storeId}
 * GET /api/internal/stores/owner/{ownerId}
 * 응답을 역직렬화합니다.
 */
@Getter
@NoArgsConstructor
public class StoreInfo {

    private Long id;
    private String storeName;
    private Long ownerId;
}
