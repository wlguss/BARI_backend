package com.bari.order.dto.request;

import com.bari.order.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 주문 상태 변경 요청 DTO (매장 전용).
 */
@Getter
@NoArgsConstructor
public class UpdateOrderStatusRequest {

    @NotNull(message = "변경할 상태는 필수입니다.")
    private OrderStatus status;
}
