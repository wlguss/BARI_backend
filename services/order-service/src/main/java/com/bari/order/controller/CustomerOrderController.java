package com.bari.order.controller;

import com.bari.common.response.ApiResponse;
import com.bari.order.dto.request.ReserveRequest;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 고객 주문 API 컨트롤러.
 * 모든 엔드포인트는 인증이 필요합니다 (api-gateway에서 X-User-Id 헤더 주입).
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "고객 - 주문 API")
@SecurityRequirement(name = "bearerAuth")
public class CustomerOrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "본인의 주문 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(
            @CurrentUserId Long customerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            @ParameterObject Pageable pageable) {
        return ApiResponse.success(orderService.getMyOrders(customerId, pageable));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "본인의 주문 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrder(
            @CurrentUserId Long customerId,
            @PathVariable Long orderId) {
        return ApiResponse.success(orderService.getMyOrder(customerId, orderId));
    }

    @PostMapping("/reserve")
    @Operation(summary = "픽업 예약", description = "상품 픽업을 예약합니다. 예약 후 주문 상태는 PENDING입니다.")
    public ResponseEntity<ApiResponse<OrderResponse>> reserve(
            @CurrentUserId Long customerId,
            @RequestBody @Valid ReserveRequest request) {
        return ApiResponse.created(orderService.reserve(customerId, request));
    }

    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "PENDING 또는 CONFIRMED 상태의 주문을 취소합니다.")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @CurrentUserId Long customerId,
            @PathVariable Long orderId) {
        return ApiResponse.success(orderService.cancelOrder(customerId, orderId));
    }
}
