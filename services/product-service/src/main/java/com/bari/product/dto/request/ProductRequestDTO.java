package com.bari.product.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequestDTO {

    @NotNull(message = "storeId는 필수입니다.")
    private Long storeId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    private String description;

    private String imageUrl;
}