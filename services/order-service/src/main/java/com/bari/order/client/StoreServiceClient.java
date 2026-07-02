package com.bari.order.client;

import com.bari.common.exception.BusinessException;
import com.bari.order.dto.client.StoreInfo;
import com.bari.order.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * store-service 내부 API 클라이언트.
 * order-service에서 매장 존재 여부 검증 및 ownerId → storeId 조회 시 사용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StoreServiceClient {

    // new를 사용하지 않고 Spring Bean으로 주입받도록 함 
    private final RestClient storeRestClient;

    /**
     * 매장 존재 여부 검증.
     * store-service: GET /api/internal/stores/{storeId}
     *
     * @param storeId 검증할 매장 ID
     * @return 매장 정보
     * @throws BusinessException STORE_NOT_FOUND — 존재하지 않거나 삭제된 매장
     */
    public StoreInfo getStore(Long storeId) {
        log.debug("store-service 매장 조회 요청 - storeId: {}", storeId);

        return storeRestClient.get()
                .uri("/api/internal/stores/{storeId}", storeId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.warn("store-service 매장 조회 실패 - storeId: {}, status: {}", storeId, response.getStatusCode());
                    throw new BusinessException(OrderErrorCode.STORE_NOT_FOUND);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("store-service 서버 오류 - storeId: {}, status: {}", storeId, response.getStatusCode());
                    throw new BusinessException(OrderErrorCode.STORE_SERVICE_UNAVAILABLE);
                })
                .body(new ParameterizedTypeReference<ApiResponseWrapper<StoreInfo>>() {})
                .getData();
    }

    /**
     * ownerId로 매장 조회 (사장님 userId → storeId 변환).
     * store-service: GET /api/internal/stores/owner/{ownerId}
     *
     * @param ownerId 사장님 userId
     * @return 매장 정보
     * @throws BusinessException STORE_NOT_FOUND — 해당 사장님의 매장이 없는 경우
     */
    public StoreInfo getStoreByOwnerId(Long ownerId) {
        log.debug("store-service ownerId로 매장 조회 요청 - ownerId: {}", ownerId);

        return storeRestClient.get()
                .uri("/api/internal/stores/owner/{ownerId}", ownerId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.warn("store-service ownerId 매장 조회 실패 - ownerId: {}, status: {}", ownerId, response.getStatusCode());
                    throw new BusinessException(OrderErrorCode.STORE_NOT_FOUND);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("store-service 서버 오류 - ownerId: {}, status: {}", ownerId, response.getStatusCode());
                    throw new BusinessException(OrderErrorCode.STORE_SERVICE_UNAVAILABLE);
                })
                .body(new ParameterizedTypeReference<ApiResponseWrapper<StoreInfo>>() {})
                .getData();
    }
}
