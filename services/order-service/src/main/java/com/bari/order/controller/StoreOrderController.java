package com.bari.order.controller;

import com.bari.common.response.ApiResponse;
import com.bari.order.dto.request.UpdateOrderStatusRequest;
import com.bari.order.dto.response.OrderResponse;
import com.bari.order.service.OrderService;
import com.bari.security.annotation.CurrentUserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 매장 주문 관리 API 컨트롤러 (사장님 전용).
 * 모든 엔드포인트는 인증이 필요합니다 (api-gateway에서 X-User-Id 헤더 주입).
 *
 * NOTE: store-service 연동 전까지 /api/store/orders/** 는 동작하지 않습니다.
 * store-service 작업 완료 후 OrderService.fetchStoreIdByOwnerId() 구현 필요.
 */
@RestController
@RequestMapping("/api/store/orders")
@RequiredArgsConstructor
@Tag(name = "매장 - 주문 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class StoreOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "본인 매장의 주문 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getStoreOrders(
            @CurrentUserId Long ownerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable) {
        return ApiResponse.success(orderService.getStoreOrders(ownerId, pageable));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "본인 매장의 주문 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<OrderResponse>> getStoreOrder(
            @CurrentUserId Long ownerId,
            @PathVariable Long orderId) {
        return ApiResponse.success(orderService.getStoreOrder(ownerId, orderId));
    }

    @PatchMapping("/{orderId}")
    @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다. CANCELLED로는 변경 불가 (고객 취소 전용).")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @CurrentUserId Long ownerId,
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateOrderStatusRequest request) {
        return ApiResponse.success(orderService.updateOrderStatus(ownerId, orderId, request));
    }
}
