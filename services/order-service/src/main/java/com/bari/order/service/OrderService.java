package com.bari.order.service;

import com.bari.common.exception.BusinessException;
import com.bari.order.dto.request.ReserveRequest;
import com.bari.order.dto.request.UpdateOrderStatusRequest;
import com.bari.order.dto.response.OrderResponse;
import com.bari.order.entity.Order;
import com.bari.order.entity.OrderStatus;
import com.bari.order.exception.OrderErrorCode;
import com.bari.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 주문 비즈니스 로직 서비스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // ========== 고객 API ==========

    /**
     * 고객 주문 목록 조회.
     * 본인의 주문만 조회합니다 (삭제된 주문 제외).
     *
     * @param customerId 고객 ID (X-User-Id 헤더에서 추출)
     * @param pageable   페이지네이션
     */
    public Page<OrderResponse> getMyOrders(Long customerId, Pageable pageable) {
        return orderRepository.findByCustomerId(customerId, pageable)
                .map(OrderResponse::from);
    }

    /**
     * 고객 주문 단건 조회.
     * 본인 주문만 조회 가능합니다.
     *
     * @param customerId 고객 ID (X-User-Id 헤더에서 추출)
     * @param orderId    주문 ID
     * @throws BusinessException ORDER_NOT_FOUND  — 존재하지 않는 주문
     * @throws BusinessException ORDER_FORBIDDEN  — 본인 주문이 아닌 경우
     */
    public OrderResponse getMyOrder(Long customerId, Long orderId) {
        Order order = findOrderOrThrow(orderId);
        validateCustomerAccess(order, customerId);
        return OrderResponse.from(order);
    }

    /**
     * 픽업 예약 (주문 생성).
     *
     * @param customerId 고객 ID (X-User-Id 헤더에서 추출)
     * @param request    예약 요청 DTO
     */
    @Transactional
    public OrderResponse reserve(Long customerId, ReserveRequest request) {
        // TODO: store-service 연동 - 매장 존재 여부 및 영업 상태 검증 (동기)
        // store-service: GET /api/internal/stores/{storeId}

        // TODO: product-service 연동 - 상품 존재 여부 검증 (동기)
        // product-service: GET /api/internal/products/{productId}

        // TODO: inventory-service 연동 - 재고 수량 확인 (동기)
        // inventory-service: GET /api/internal/inventory/{productId}
        // 재고 부족 시 예외 처리 필요 (예: INVENTORY_NOT_ENOUGH)

        Order order = request.toEntity(customerId);
        Order saved = orderRepository.save(order);
        log.info("픽업 예약 완료 - orderId: {}, customerId: {}, storeId: {}", saved.getId(), customerId, saved.getStoreId());
        return OrderResponse.from(saved);
    }

    /**
     * 주문 취소 (고객).
     * PENDING 또는 CONFIRMED 상태인 주문만 취소 가능합니다.
     *
     * @param customerId 고객 ID (X-User-Id 헤더에서 추출)
     * @param orderId    주문 ID
     * @throws BusinessException ORDER_NOT_FOUND        — 존재하지 않는 주문
     * @throws BusinessException ORDER_FORBIDDEN        — 본인 주문이 아닌 경우
     * @throws BusinessException ORDER_ALREADY_CANCELLED — 이미 취소된 주문
     * @throws BusinessException ORDER_CANNOT_CANCEL    — 취소 불가능한 상태
     */
    @Transactional
    public OrderResponse cancelOrder(Long customerId, Long orderId) {
        Order order = findOrderOrThrow(orderId);
        validateCustomerAccess(order, customerId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(OrderErrorCode.ORDER_ALREADY_CANCELLED);
        }
        if (!order.isCancellable()) {
            throw new BusinessException(OrderErrorCode.ORDER_CANNOT_CANCEL);
        }

        // TODO: inventory-service 연동 - 재고 복구 이벤트 발행 (Kafka)
        // KafkaTemplate으로 "order.cancelled" 토픽에 이벤트 발행

        order.cancel();
        log.info("주문 취소 완료 - orderId: {}, customerId: {}", orderId, customerId);
        return OrderResponse.from(order);
    }

    // ========== 매장 API ==========

    /**
     * 매장 주문 목록 조회 (매장 사장님 전용).
     *
     * @param ownerId  사장님 ID (X-User-Id 헤더에서 추출)
     * @param pageable 페이지네이션
     */
    public Page<OrderResponse> getStoreOrders(Long ownerId, Pageable pageable) {
        // TODO: store-service 연동 - ownerId로 소유 storeId 조회
        // store-service: GET /api/internal/stores/owner/{ownerId}
        // 예: Long storeId = storeServiceClient.getStoreIdByOwnerId(ownerId);
        Long storeId = fetchStoreIdByOwnerId(ownerId);

        return orderRepository.findByStoreId(storeId, pageable)
                .map(OrderResponse::from);
    }

    /**
     * 매장 주문 단건 조회 (매장 사장님 전용).
     *
     * @param ownerId 사장님 ID (X-User-Id 헤더에서 추출)
     * @param orderId 주문 ID
     * @throws BusinessException ORDER_NOT_FOUND — 존재하지 않는 주문
     * @throws BusinessException ORDER_FORBIDDEN — 본인 매장 주문이 아닌 경우
     */
    public OrderResponse getStoreOrder(Long ownerId, Long orderId) {
        // TODO: store-service 연동 - ownerId로 소유 storeId 조회
        Long storeId = fetchStoreIdByOwnerId(ownerId);

        Order order = findOrderOrThrow(orderId);
        validateStoreAccess(order, storeId);
        return OrderResponse.from(order);
    }

    /**
     * 주문 상태 변경 (매장 사장님 전용).
     * CANCELLED 상태로는 이 API로 변경 불가 (고객 취소 API 전용).
     *
     * @param ownerId 사장님 ID (X-User-Id 헤더에서 추출)
     * @param orderId 주문 ID
     * @param request 상태 변경 요청
     * @throws BusinessException ORDER_NOT_FOUND          — 존재하지 않는 주문
     * @throws BusinessException ORDER_FORBIDDEN          — 본인 매장 주문이 아닌 경우
     * @throws BusinessException ORDER_CANNOT_UPDATE_STATUS — CANCELLED 상태로 변경 시도
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long ownerId, Long orderId, UpdateOrderStatusRequest request) {
        // TODO: store-service 연동 - ownerId로 소유 storeId 조회
        Long storeId = fetchStoreIdByOwnerId(ownerId);

        Order order = findOrderOrThrow(orderId);
        validateStoreAccess(order, storeId);

        // 매장은 CANCELLED 상태로 직접 변경 불가 (취소는 별도 프로세스로 처리)
        if (request.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException(OrderErrorCode.ORDER_CANNOT_UPDATE_STATUS);
        }

        order.updateStatus(request.getStatus());
        log.info("주문 상태 변경 완료 - orderId: {}, status: {}, storeId: {}", orderId, request.getStatus(), storeId);
        return OrderResponse.from(order);
    }

    // ========== private 헬퍼 ==========

    private Order findOrderOrThrow(Long orderId) {
        return orderRepository.findByIdAndDeletedAtIsNull(orderId)
                .orElseThrow(() -> new BusinessException(OrderErrorCode.ORDER_NOT_FOUND));
    }

    private void validateCustomerAccess(Order order, Long customerId) {
        if (!order.isOwnedBy(customerId)) {
            throw new BusinessException(OrderErrorCode.ORDER_FORBIDDEN);
        }
    }

    private void validateStoreAccess(Order order, Long storeId) {
        if (!order.belongsToStore(storeId)) {
            throw new BusinessException(OrderErrorCode.ORDER_FORBIDDEN);
        }
    }

    /**
     * TODO: store-service 연동 후 구현.
     * store-service에서 해당 ownerId(사장님 userId)의 storeId를 조회합니다.
     *
     * 연동 방법: RestClient 또는 OpenFeign
     * 호출 예시: GET /api/internal/stores/owner/{ownerId}
     */
    private Long fetchStoreIdByOwnerId(Long ownerId) {
        // TODO: store-service RestClient/Feign 호출로 교체
        // return storeServiceClient.getStoreIdByOwnerId(ownerId);
        throw new UnsupportedOperationException(
                "store-service 연동 전입니다. store-service 작업자에게 문의하세요. ownerId=" + ownerId
        );
    }
}
