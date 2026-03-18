package com.bari.item.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 아이템 생성/수정 요청 DTO.
 *
 * 예시 요청 JSON:
 * {
 *   "name": "사과",
 *   "description": "신선한 사과입니다.",
 *   "price": 3000
 * }
 */
@Getter
@NoArgsConstructor
public class ItemRequest {

    /** 아이템 이름 (필수) */
    @NotBlank(message = "아이템 이름은 필수입니다.")
    private String name;

    /** 아이템 설명 (선택) */
    private String description;

    /** 가격 (0 이상) */
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;
}
