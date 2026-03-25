package com.bari.discount.dto.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * store-service 내부 API 응답 매핑용 DTO.
 */
@Getter
@NoArgsConstructor
public class StoreInfo {
    private Long id;
    private String storeName;
    private String description;
    private String address;
    private String phone;
    private String businessHours;
    private String category;
    private String imageUrl;
}
