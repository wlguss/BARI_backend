package com.bari.order.client;

import com.bari.common.exception.BusinessException;
import com.bari.order.dto.client.InventoryInfo;
import com.bari.order.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * inventory-service API 클라이언트.
 * order-service에서 재고 수량 확인 시 사용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryServiceClient {

    private final RestClient inventoryRestClient;

    /**
     * 상품 재고 목록 조회.
     * inventory-service: GET /api/store/inventory/product/{productId}
     * inventory-service는 X-User-Id 헤더 인증이 필요하므로 요청 사용자 ID를 함께 전달합니다.
     *
     * @param productId    재고를 확인할 상품 ID
     * @param requestUserId 요청 사용자 ID (인증 헤더용)
     * @return 해당 상품의 재고 목록
     * @throws BusinessException INVENTORY_SERVICE_UNAVAILABLE — inventory-service 서버 오류
     */
    public List<InventoryInfo> getInventoriesByProduct(Long productId, Long requestUserId) {
        log.debug("inventory-service 재고 조회 요청 - productId: {}", productId);

        return inventoryRestClient.get()
                .uri("/api/store/inventory/product/{productId}", productId)
                .header("X-User-Id", String.valueOf(requestUserId))
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("inventory-service 서버 오류 - productId: {}, status: {}", productId, response.getStatusCode());
                    throw new BusinessException(OrderErrorCode.INVENTORY_SERVICE_UNAVAILABLE);
                })
                .body(new ParameterizedTypeReference<ApiResponseWrapper<List<InventoryInfo>>>() {})
                .getData();
    }
}
