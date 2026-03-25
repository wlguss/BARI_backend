package com.bari.user.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * store-service 내부 API 응답 매핑용 DTO.
 * GET /api/internal/stores/owner/{ownerId}
 */
@Getter
@NoArgsConstructor
public class StoreInfo {
    private Long id;
    private String storeName;
    private Long ownerId;
}
