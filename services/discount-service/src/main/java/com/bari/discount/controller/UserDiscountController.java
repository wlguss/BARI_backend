package com.bari.discount.controller;

import com.bari.common.response.ApiResponse;
import com.bari.discount.dto.response.DiscountDetailResponse;
import com.bari.discount.dto.response.StoreDiscountResponse;
import com.bari.discount.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 유저용 할인 목록 조회 API 컨트롤러.
 */
@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
@Tag(name = "유저 - 할인 목록 API")
public class UserDiscountController {

    private final DiscountService discountService;

    @GetMapping
    @Operation(summary = "전체 할인 목록 조회", description = "모든 매장의 활성 할인 목록을 조회합니다. 상품 이미지 포함.")
    public ResponseEntity<ApiResponse<List<StoreDiscountResponse>>> getAllDiscounts() {
        return ResponseEntity.ok(ApiResponse.success(discountService.getAllDiscounts()));
    }

    @GetMapping("/{discountId}")
    @Operation(summary = "할인 상품 상세 조회", description = "할인 정보, 재고 정보, 상품 정보, 매장 정보를 모두 반환합니다.")
    public ResponseEntity<ApiResponse<DiscountDetailResponse>> getDiscountDetail(@PathVariable Long discountId) {
        return ResponseEntity.ok(ApiResponse.success(discountService.getDiscountDetail(discountId)));
    }
}
