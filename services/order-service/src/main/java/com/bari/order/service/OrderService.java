package com.bari.order.service;

import com.bari.common.exception.BusinessException;
import com.bari.order.client.InventoryServiceClient;
import com.bari.order.client.ProductServiceClient;
import com.bari.order.client.StoreServiceClient;
import com.bari.order.dto.client.InventoryInfo;
import com.bari.order.dto.client.ProductInfo;
import com.bari.order.dto.client.StoreInfo;

import java.util.List;
import com.bari.order.dto.request.ReserveRequest;
import com.bari.order.dto.request.UpdateOrderStatusRequest;
import com.bari.order.dto.response.OrderResponse;
import com.bari.order.entity.Order;
import com.bari.order.entity.OrderStatus;
import com.bari.order.event.OrderCancelledEvent;
import com.bari.order.event.OrderReservedEvent;
import com.bari.order.exception.OrderErrorCode;
import com.bari.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
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

    private static final String TOPIC_ORDER_RESERVED  = "order.reserved";
    private static final String TOPIC_ORDER_CANCELLED = "order.cancelled";

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductServiceClient productServiceClient;
    private final StoreServiceClient storeServiceClient;
    private final InventoryServiceClient inventoryServiceClient;

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
        // 매장 존재 여부 검증 (store-service 동기 호출)
        StoreInfo store = storeServiceClient.getStore(request.getStoreId());
        log.debug("매장 검증 완료 - storeId: {}, name: {}", store.getId(), store.getStoreName());

        // 상품 존재 여부 검증 (product-service 동기 호출)
        ProductInfo product = productServiceClient.getProduct(request.getProductId());
        log.debug("상품 검증 완료 - productId: {}, name: {}", product.getId(), product.getName());

        // 재고 수량 확인 (inventory-service 동기 호출)
        List<InventoryInfo> inventories = inventoryServiceClient.getInventoriesByProduct(request.getProductId(), customerId);
        int totalStock = inventories.stream().mapToInt(InventoryInfo::getQuantity).sum();
        if (totalStock < request.getQuantity()) {
            log.warn("재고 부족 - productId: {}, 요청 수량: {}, 현재 재고: {}", request.getProductId(), request.getQuantity(), totalStock);
            throw new BusinessException(OrderErrorCode.INVENTORY_NOT_ENOUGH);
        }
        log.debug("재고 확인 완료 - productId: {}, 요청 수량: {}, 현재 재고: {}", request.getProductId(), request.getQuantity(), totalStock);

        Order order = request.toEntity(customerId);
        Order saved = orderRepository.save(order);

        // 재고 차감 이벤트 발행 → inventory-service가 수신하여 처리
        kafkaTemplate.send(TOPIC_ORDER_RESERVED, String.valueOf(saved.getId()), OrderReservedEvent.from(saved));
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

        order.cancel();

        // 재고 복구 이벤트 발행 → inventory-service가 수신하여 처리
        kafkaTemplate.send(TOPIC_ORDER_CANCELLED, String.valueOf(order.getId()), OrderCancelledEvent.from(order));
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

    /**
     * 매장 기준 전체 주문 목록 조회 (storeId 직접 지정).
     * status가 null이면 전체, 값이 있으면 해당 status만 필터링합니다.
     *
     * @param storeId 매장 ID
     * @param status  주문 상태 필터 (null 허용)
     */
    public List<OrderResponse> getOrdersByStoreId(Long storeId, OrderStatus status) {
        if (status != null) {
            return orderRepository.findAllByStoreIdAndStatus(storeId, status).stream()
                    .map(OrderResponse::from)
                    .toList();
        }
        return orderRepository.findAllByStoreId(storeId).stream()
                .map(OrderResponse::from)
                .toList();
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
     * store-service에서 해당 ownerId(사장님 userId)의 storeId를 조회합니다.
     * store-service: GET /api/internal/stores/owner/{ownerId}
     */
    private Long fetchStoreIdByOwnerId(Long ownerId) {
        StoreInfo store = storeServiceClient.getStoreByOwnerId(ownerId);
        log.debug("ownerId로 storeId 조회 완료 - ownerId: {}, storeId: {}", ownerId, store.getId());
        return store.getId();
    }
}
