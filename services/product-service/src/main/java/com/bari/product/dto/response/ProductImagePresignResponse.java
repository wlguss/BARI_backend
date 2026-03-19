package com.bari.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImagePresignResponse {

    private String uploadUrl;
    private String imageUrl;
    private String key;
}
