package com.bari.product.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImagePresignResponse {

    private String presignedUrl;
    private String key;
    private String imageUrl;
    private Long expiresIn;
}
