package com.bari.product.controller;

import com.bari.product.dto.response.ProductResponseDTO;
import com.bari.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서비스 간 내부 통신 전용 컨트롤러.
 * 외부에 노출되지 않으며 api-gateway에서 라우팅하지 않습니다.
 * k8s 환경에서는 ClusterIP를 통해 내부 서비스끼리만 접근 가능합니다.
 */
@RestController
@RequestMapping("/api/internal/products")
@RequiredArgsConstructor
@Tag(name = "Internal Product API", description = "서비스 간 내부 통신 전용 (외부 호출 금지)")
public class InternalProductController {

    private final ProductService productService;

    /**
     * 상품 단건 조회 (내부용).
     * order-service에서 픽업 예약 시 상품 존재 여부 검증에 사용합니다.
     *
     * @param productId 상품 ID
     * @return 상품 정보
     */
    @GetMapping("/{productId}")
    @Operation(summary = "[내부] 상품 조회", description = "order-service 등 내부 서비스에서 상품 존재 여부 확인용")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getById(productId));
    }
}
