package com.bari.product.controller;

import com.bari.product.dto.request.ProductImagePresignRequest;
import com.bari.product.dto.response.ProductImagePresignResponse;
import com.bari.product.service.ProductImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Product Image", description = "상품 이미지 업로드 관련 API")
@RestController
@RequestMapping(value = "/api/products/images", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class ProductImageController {

    private final ProductImageService productImageService;

    @Operation(
            summary = "상품 이미지 업로드용 Presigned URL 발급",
            description = """
                    클라이언트가 S3에 직접 이미지를 업로드할 수 있도록 Presigned URL을 발급한다.
                    클라이언트는 반환받은 presignedUrl로 PUT 요청을 보내 파일을 업로드하면 된다.
                    """
    )
    @PostMapping(value = "/presigned-url", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductImagePresignResponse> createPresignedUploadUrl(
            @Valid @RequestBody ProductImagePresignRequest request
    ) {
        ProductImagePresignResponse response = productImageService.generatePresignedUploadUrl(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}