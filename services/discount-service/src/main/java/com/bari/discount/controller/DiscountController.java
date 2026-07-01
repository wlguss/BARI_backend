package com.bari.discount.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bari.common.response.ApiResponse;
import com.bari.discount.dto.request.DiscountRequest;
import com.bari.discount.service.DiscountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // RQ-4005 매장 기준 할인 전체 목록 조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<?>> getDiscountsByStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(discountService.getDiscountsByStore(storeId)));
    }

    // 상세 조회
    @GetMapping("/{inventoryId}")
    public ResponseEntity<ApiResponse<?>> getDiscount(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(ApiResponse.success(discountService.get(inventoryId)));
    }

    // 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@RequestBody DiscountRequest dto) {
        discountService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>created(null));
    }

    // 수정
    @PutMapping("/{discountId}")
    public ResponseEntity<ApiResponse<?>> update(
            @PathVariable Long discountId,
            @RequestBody DiscountRequest dto) {

        return ResponseEntity.ok(ApiResponse.success(discountService.update(discountId, dto)));
    }

    // 삭제 (종료 + soft delete)
    @DeleteMapping("/{discountId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long discountId) {
        discountService.delete(discountId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
