package com.bari.inventory.controller;

import com.bari.common.response.ApiResponse;
import com.bari.inventory.dto.response.InventoryResponse;
import com.bari.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 서비스 간 내부 통신 전용 컨트롤러.
 * 외부에 노출되지 않으며 api-gateway에서 라우팅하지 않습니다.
 * k8s 환경에서는 ClusterIP를 통해 내부 서비스끼리만 접근 가능합니다.
 */
@RestController
@RequestMapping("/api/internal/inventories")
@RequiredArgsConstructor
@Tag(name = "Internal Inventory API", description = "서비스 간 내부 통신 전용 (외부 호출 금지)")
public class InternalInventoryController {

    private final InventoryService inventoryService;

    /**
     * 여러 상품의 재고 목록 조회 (내부용).
     * discount-service에서 찜한 매장의 할인 임박 상품 조회 시 사용합니다.
     *
     * @param productIds 상품 ID 목록
     * @return 재고 목록
     */
    @GetMapping("/by-products")
    @Operation(summary = "[내부] 상품별 재고 목록 조회", description = "discount-service에서 찜한 매장 할인 임박 상품 조회용")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoriesByProductIds(@RequestParam List<Long> productIds) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findByProductIds(productIds)));
    }

    @GetMapping("/by-ids")
    @Operation(summary = "[내부] 재고 ID 목록으로 재고 조회", description = "discount-service 전체 할인 목록 조회용")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> getInventoriesByIds(@RequestParam List<Long> inventoryIds) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findByIds(inventoryIds)));
    }
}
