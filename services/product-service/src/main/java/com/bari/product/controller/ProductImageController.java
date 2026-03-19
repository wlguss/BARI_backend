package com.bari.product.controller;

import com.bari.product.dto.request.ProductImagePresignRequest;
import com.bari.product.dto.response.ProductImagePresignResponse;
import com.bari.product.service.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping("/presigned-url")
    public ResponseEntity<ProductImagePresignResponse> createPresignedUploadUrl(
            @Valid @RequestBody ProductImagePresignRequest request
    ) {
        return ResponseEntity.ok(
                productImageService.generatePresignedUploadUrl(request)
        );
    }
}