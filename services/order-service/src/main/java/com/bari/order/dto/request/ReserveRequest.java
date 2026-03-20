package com.bari.order.dto.request;

import com.bari.order.entity.Order;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 픽업 예약 요청 DTO.
 */
@Getter
@NoArgsConstructor
public class ReserveRequest {

    /**
     * 매장 ID.
     * TODO: store-service 연동 시 해당 매장 존재 여부 검증 필요
     */
    @NotNull(message = "매장 ID는 필수입니다.")
    private Long storeId;

    /**
     * 상품 ID.
     * TODO: product-service 연동 시 해당 상품 존재 여부 및 재고 검증 필요
     * TODO: inventory-service 연동 시 예약 후 재고 차감 이벤트 발행 필요 (Kafka)
     */
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    private Integer quantity;

    @NotNull(message = "픽업 시간은 필수입니다.")
    @Future(message = "픽업 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime pickupTime;

    /** DTO → Entity */
    public Order toEntity(Long customerId) {
        return Order.reserve(customerId, storeId, productId, quantity, pickupTime);
    }
}
