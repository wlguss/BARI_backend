package com.bari.inventory.controller;

import java.time.LocalDateTime;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> createInventory(@RequestBody InventoryRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryService.create(dto));
    }

    // RQ-3002 재고 조회 (상품 기준)
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getInventories(
            @PathVariable Long productId) {

        // if (productId != null) {
        // return ResponseEntity.ok(inventoryService.findByProduct(productId));
        // }
        return ResponseEntity.ok(inventoryService.findByProduct(productId));
        // 전체 조회 (확장성 고려)
        // return ResponseEntity.ok(inventoryService.findAll());
    }

    // RQ-3003 재고 수정
    @PutMapping("/{inventoryId}")
    public ResponseEntity<?> updateInventory(@PathVariable Long inventoryId,
            @RequestBody InventoryUpdateRequest dto) {

        inventoryService.update(inventoryId, dto);
        return ResponseEntity.ok().build();
    }

    // RQ-3004 재고 삭제 (soft delete)
    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.delete(inventoryId);
        return ResponseEntity.ok().build();
    }

    // RQ-3005 유통기한 만료 처리
    @PatchMapping("/expire")
    public ResponseEntity<?> expireInventories() {
        inventoryService.expireInventories();
        return ResponseEntity.ok().build();
    }

    // RQ-3006 유통기한 임박 상품 조회
    @GetMapping("/near-expire")
    public ResponseEntity<?> getNearExpireInventories() {
        return ResponseEntity.ok(inventoryService.findNearExpire());
    }
}