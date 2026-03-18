package com.bari.item.dto.response;

import com.bari.item.entity.Item;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 아이템 응답 DTO.
 *
 * 응답 예시:
 * {
 *   "id": 1,
 *   "name": "사과",
 *   "description": "신선한 사과입니다.",
 *   "price": 3000,
 *   "createdBy": 1,
 *   "createdAt": "2024-01-01T00:00:00"
 * }
 */
@Getter
@Builder
public class ItemResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final int price;

    /**
     * 생성자 ID — X-User-Id 헤더에서 추출한 userId
     * user-service를 별도로 호출하지 않고 헤더 값을 그대로 사용합니다.
     */
    private final Long createdBy;

    private final LocalDateTime createdAt;

    /**
     * Item 엔티티에서 ItemResponse 생성.
     *
     * @param item Item 엔티티
     * @return ItemResponse
     */
    public static ItemResponse from(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .createdBy(item.getCreatedBy())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
