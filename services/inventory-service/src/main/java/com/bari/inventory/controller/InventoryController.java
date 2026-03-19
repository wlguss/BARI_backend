package com.bari.inventory.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bari.inventory.dto.request.InventoryRequest;
import com.bari.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // 1. 인벤토리 등록
    @PostMapping
    public ResponseEntity<?> createInventory(@RequestBody InventoryRequest dto) {
        return ResponseEntity.ok(inventoryService.create(dto));
    }

    // 2. 인벤토리 목록 조회 (productId 기준)
    @GetMapping
    public ResponseEntity<?> getInventories(@RequestParam Long productId) {
        return ResponseEntity.ok(inventoryService.findByProduct(productId));
    }

    // 3. 인벤토리 수정
    @PutMapping("/{itemId}")
    public ResponseEntity<?> updateInventory(
            @PathVariable Long itemId,
            @RequestParam Integer quantity,
            @RequestParam String expireAt) {

        inventoryService.update(
                itemId,
                quantity,
                LocalDateTime.parse(expireAt));

        return ResponseEntity.ok().build();
    }

    // 4. 인벤토리 삭제
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long itemId) {
        inventoryService.delete(itemId);
        return ResponseEntity.ok().build();
    }
}