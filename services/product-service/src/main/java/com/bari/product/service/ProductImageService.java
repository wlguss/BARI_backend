package com.bari.product.service;

import com.bari.product.dto.request.ProductImagePresignRequest;
import com.bari.product.dto.response.ProductImagePresignResponse;

public interface ProductImageService {
    ProductImagePresignResponse generatePresignedUploadUrl(ProductImagePresignRequest request);
}