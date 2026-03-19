package com.bari.product.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateDTO {

    private String name;
    private String description;
    private String imageUrl;
}