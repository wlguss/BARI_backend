package com.bari.store.controller;

import com.bari.common.response.ApiResponse;
import com.bari.security.annotation.CurrentUserId;
import com.bari.store.dto.client.ExpiringDiscountInfo;
import com.bari.store.dto.response.FavoriteStoreResponse;
import com.bari.store.service.FavoriteStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Favorite Store API", description = "찜한 매장 관리")
public class FavoriteStoreController {

    private final FavoriteStoreService favoriteStoreService;

    /**
     * 찜하기 / 찜해제 토글.
     * - 찜 상태면 해제, 해제 상태면 다시 찜하기
     */
    @PostMapping("/{storeId}/favorite")
    @Operation(summary = "찜하기 / 찜해제 토글")
    public ResponseEntity<ApiResponse<Void>> toggleFavorite(
            @PathVariable Long storeId,
            @CurrentUserId Long userId) {
        favoriteStoreService.toggleFavorite(userId, storeId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 찜한 매장 목록 조회.
     */
    @GetMapping("/favorites")
    @Operation(summary = "찜한 매장 목록 조회")
    public ResponseEntity<ApiResponse<List<FavoriteStoreResponse>>> getFavoriteStores(
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(favoriteStoreService.getFavoriteStores(userId)));
    }

    /**
     * 찜한 매장의 할인 임박 상품 목록 조회 (홈화면용).
     * 내일 마감까지 등록된 할인 상품만 반환합니다.
     */
    @GetMapping("/favorites/discounts")
    @Operation(summary = "찜한 매장 할인 임박 상품 목록 조회", description = "홈화면 노출용 — 내일 마감 기준")
    public ResponseEntity<ApiResponse<List<ExpiringDiscountInfo>>> getExpiringDiscounts(
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(favoriteStoreService.getExpiringDiscounts(userId)));
    }
}
