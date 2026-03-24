package com.bari.store.controller;

import com.bari.common.response.ApiResponse;
import com.bari.security.annotation.CurrentUserId;
import com.bari.store.dto.request.StoreRequestDto;
import com.bari.store.dto.response.StoreResponseDto;
import com.bari.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "Store API", description = "매장 관리")
public class StoreController {

    private final StoreService storeService;

    /**
     * 1. 매장 전체 목록 조회
     */
    @GetMapping
    @Operation(summary = "매장 전체 목록 조회")
    public ResponseEntity<ApiResponse<List<StoreResponseDto>>> getAllStores() {
        return ResponseEntity.ok(ApiResponse.success(storeService.getAllStores()));
    }

    /**
     * 2. 특정 매장 상세 조회
     */
    @GetMapping("/{id}")
    @Operation(summary = "매장 상세 조회")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getStoreDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreDetail(id)));
    }

    /**
     * 3. 매장 신규 등록
     */
    @PostMapping
    @Operation(summary = "매장 등록")
    public ResponseEntity<ApiResponse<Long>> createStore(
            @CurrentUserId Long userId,
            @RequestBody StoreRequestDto requestDto) {
        Long storeId = storeService.createStore(userId, requestDto);
        return ResponseEntity.status(201).body(ApiResponse.created(storeId));
    }

    /**
     * 4. 매장 정보 수정
     */
    @PutMapping("/{id}")
    @Operation(summary = "매장 정보 수정")
    public ResponseEntity<ApiResponse<Void>> updateStore(
            @PathVariable Long id,
            @RequestBody StoreRequestDto requestDto) {
        storeService.updateStore(id, requestDto);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 5. 매장 삭제 (Soft Delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "매장 삭제")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
