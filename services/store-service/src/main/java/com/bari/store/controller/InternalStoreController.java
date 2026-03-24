package com.bari.store.controller;

import com.bari.common.response.ApiResponse;
import com.bari.store.dto.response.StoreResponseDto;
import com.bari.store.service.StoreService;
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
@RequestMapping("/api/internal/stores")
@RequiredArgsConstructor
@Tag(name = "Internal Store API", description = "서비스 간 내부 통신 전용 (외부 호출 금지)")
public class InternalStoreController {

    private final StoreService storeService;

    /**
     * 매장 단건 조회 (내부용).
     * order-service에서 픽업 예약 시 매장 존재 여부 검증에 사용합니다.
     */
    @GetMapping("/{storeId}")
    @Operation(summary = "[내부] 매장 조회", description = "order-service 등 내부 서비스에서 매장 존재 여부 확인용")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getStore(@PathVariable Long storeId) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreForInternal(storeId)));
    }

    /**
     * ownerId로 매장 조회 (내부용).
     * order-service에서 매장 사장님의 storeId 조회 시 사용합니다.
     */
    @GetMapping("/owner/{ownerId}")
    @Operation(summary = "[내부] ownerId로 매장 조회", description = "order-service에서 사장님 userId로 storeId 조회용")
    public ResponseEntity<ApiResponse<StoreResponseDto>> getStoreByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(ApiResponse.success(storeService.getStoreByOwnerId(ownerId)));
    }
}
