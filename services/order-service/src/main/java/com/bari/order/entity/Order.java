package com.bari.order.entity;

import com.bari.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 주문 엔티티.
 * soft delete 지원 (BaseTimeEntity.deletedAt 사용)
 */
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 주문한 고객 ID — api-gateway에서 주입된 X-User-Id 헤더 값.
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 주문 대상 매장 ID.
     * TODO: store-service 연동 시 storeId 유효성 검증 필요
     * store-service: GET /api/internal/stores/{storeId}
     */
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    /**
     * 주문 상품 ID.
     * TODO: product-service 연동 시 productId 유효성 검증 필요
     * product-service: GET /api/internal/products/{productId}
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 주문 수량 */
    @Column(nullable = false)
    private Integer quantity;

    /** 주문 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    /** 고객이 요청한 픽업 예정 시간 */
    @Column(name = "pickup_time", nullable = false)
    private LocalDateTime pickupTime;

    // ========== 정적 팩토리 ==========

    /**
     * 픽업 예약 주문 생성.
     * 최초 상태는 PENDING으로 시작합니다.
     *
     * @param customerId 주문 고객 ID (X-User-Id 헤더에서 추출)
     * @param storeId    매장 ID
     * @param productId  상품 ID
     * @param quantity   주문 수량
     * @param pickupTime 픽업 예정 시간
     */
    public static Order reserve(Long customerId, Long storeId, Long productId,
                                Integer quantity, LocalDateTime pickupTime) {
        Order order = new Order();
        order.customerId = customerId;
        order.storeId = storeId;
        order.productId = productId;
        order.quantity = quantity;
        order.status = OrderStatus.PENDING;
        order.pickupTime = pickupTime;
        return order;
    }

    // ========== 비즈니스 메서드 ==========

    /**
     * 주문 상태 변경 (매장 전용).
     *
     * @param status 변경할 상태
     */
    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * 주문 취소 (soft delete 포함).
     * PENDING 또는 CONFIRMED 상태에서만 취소 가능합니다.
     */
    public void cancel() {
        this.status = OrderStatus.CANCELLED;
        this.softDelete();
    }

    /** 해당 고객의 주문인지 확인 */
    public boolean isOwnedBy(Long customerId) {
        return this.customerId.equals(customerId);
    }

    /** 해당 매장의 주문인지 확인 */
    public boolean belongsToStore(Long storeId) {
        return this.storeId.equals(storeId);
    }

    /** 취소 가능한 상태인지 확인 (PENDING, CONFIRMED만 취소 가능) */
    public boolean isCancellable() {
        return this.status == OrderStatus.PENDING || this.status == OrderStatus.CONFIRMED;
    }
}
