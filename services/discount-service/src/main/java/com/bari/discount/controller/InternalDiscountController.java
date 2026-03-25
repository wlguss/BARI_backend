package com.bari.discount.controller;

import com.bari.discount.dto.response.DiscountResponse;
import com.bari.discount.dto.response.ExpiringDiscountResponse;
import com.bari.discount.service.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 서비스 간 내부 통신 전용 컨트롤러.
 * 외부에 노출되지 않으며 api-gateway에서 라우팅하지 않습니다.
 * k8s 환경에서는 ClusterIP를 통해 내부 서비스끼리만 접근 가능합니다.
 */
@RestController
@RequestMapping("/api/internal/discounts")
@RequiredArgsConstructor
@Tag(name = "Internal Discount API", description = "서비스 간 내부 통신 전용 (외부 호출 금지)")
public class InternalDiscountController {

    private final DiscountService discountService;

    /**
     * 찜한 매장의 할인 임박 상품 조회 (내부용).
     * store-service에서 홈화면 노출용으로 호출합니다.
     * 내일 마감까지 등록된 할인 상품만 반환합니다.
     *
     * @param storeIds 찜한 매장 ID 목록
     * @return 할인 임박 상품 목록
     */
    @GetMapping("/expiring")
    @Operation(summary = "[내부] 찜한 매장 할인 임박 상품 조회", description = "store-service 홈화면 노출용 (내일 마감 기준)")
    public ResponseEntity<List<ExpiringDiscountResponse>> getExpiringDiscounts(@RequestParam List<Long> storeIds) {
        return ResponseEntity.ok(discountService.getExpiringDiscountsByStoreIds(storeIds));
    }

    /**
     * 재고 ID 목록의 현재 활성 할인 조회 (내부용).
     * order-service에서 픽업 예약 시 할인 적용 가격 계산에 사용합니다.
     *
     * @param inventoryIds 재고 ID 목록
     * @return 현재 활성 할인 목록 (startAt <= now <= endAt)
     */
    @GetMapping("/active-by-inventories")
    @Operation(summary = "[내부] 재고별 활성 할인 조회", description = "order-service 주문 가격 계산용")
    public ResponseEntity<List<DiscountResponse>> getActiveDiscountsByInventoryIds(@RequestParam List<Long> inventoryIds) {
        return ResponseEntity.ok(discountService.getActiveDiscountsByInventoryIds(inventoryIds));
    }
}
