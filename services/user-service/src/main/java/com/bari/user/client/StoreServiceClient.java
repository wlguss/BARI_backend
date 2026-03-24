package com.bari.user.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * store-service 내부 API 클라이언트.
 * OWNER 로그인 시 storeId/storeName 조회에 사용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StoreServiceClient {

    private final RestClient storeRestClient;

    /**
     * ownerId로 매장 조회.
     * store-service: GET /api/internal/stores/owner/{ownerId}
     *
     * @param ownerId 사장님 userId
     * @return 매장 정보 (없으면 null)
     */
    public StoreInfo getStoreByOwnerId(Long ownerId) {
        try {
            return storeRestClient.get()
                    .uri("/api/internal/stores/owner/{ownerId}", ownerId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        log.warn("store-service 매장 조회 실패 - ownerId: {}", ownerId);
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        log.error("store-service 서버 오류 - ownerId: {}", ownerId);
                    })
                    .body(StoreInfo.class);
        } catch (Exception e) {
            // 로그인 자체가 실패하면 안 되므로 store 조회 실패 시 null 반환
            log.warn("store-service 호출 실패 (로그인은 유지) - ownerId: {}, error: {}", ownerId, e.getMessage());
            return null;
        }
    }
}
