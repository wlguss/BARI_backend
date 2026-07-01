package com.bari.inventory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bari.common.response.ApiResponse;
import com.bari.inventory.dto.request.InventoryRequest;
import com.bari.inventory.dto.request.InventoryUpdateRequest;
import com.bari.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // RQ-3001 재고 등록
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createInventory(@RequestBody InventoryRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(inventoryService.create(dto)));
    }

    // RQ-3002 재고 조회 (상품 기준)
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<?>> getInventories(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findByProduct(productId)));
    }

    // RQ-3003 재고 수정
    @PutMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<Void>> updateInventory(@PathVariable Long inventoryId,
            @RequestBody InventoryUpdateRequest dto) {

        inventoryService.update(inventoryId, dto);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // RQ-3004 재고 삭제 (soft delete)
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.delete(inventoryId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    // RQ-3005 유통기한 만료 처리
    @PatchMapping("/expire")
    public ResponseEntity<ApiResponse<Void>> expireInventories() {
        inventoryService.expireInventories();
        return ResponseEntity.ok(ApiResponse.success());
    }

    // RQ-3006 유통기한 임박 상품 조회
    @GetMapping("/near-expire")
    public ResponseEntity<ApiResponse<?>> getNearExpireInventories() {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findNearExpire()));
    }

    // RQ-3007 매장 재고 전체 목록 조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<?>> getInventoriesByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.findByStore(storeId)));
    }

    // 존재 유무
    @GetMapping("/exists/{inventoryId}")
    public ResponseEntity<ApiResponse<Boolean>> exists(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.exists(inventoryId)));
    }
}
