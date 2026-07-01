package com.bari.order.client;

import com.bari.order.dto.client.DiscountInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * discount-service 내부 API 클라이언트.
 * order-service에서 픽업 예약 시 활성 할인 가격 조회에 사용합니다.
 * 할인 조회 실패 시 빈 목록을 반환하여 주문 자체가 실패하지 않도록 처리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DiscountServiceClient {

    private final RestClient discountRestClient;

    /**
     * 재고 ID 목록의 현재 활성 할인 조회.
     * discount-service: GET /api/internal/discounts/active-by-inventories
     * 조회 실패 시 빈 목록 반환 (할인 없음으로 처리).
     *
     * @param inventoryIds 재고 ID 목록
     * @return 현재 활성 할인 목록 (실패 시 빈 목록)
     */
    public List<DiscountInfo> getActiveDiscounts(List<Long> inventoryIds) {
        try {
            ApiResponseWrapper<List<DiscountInfo>> response = discountRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/internal/discounts/active-by-inventories")
                            .queryParam("inventoryIds", inventoryIds.toArray())
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponseWrapper<List<DiscountInfo>>>() {});
            return response != null ? response.getData() : List.of();
        } catch (RestClientException e) {
            log.warn("discount-service 할인 조회 실패, 정가 적용 - inventoryIds: {}, error: {}", inventoryIds, e.getMessage());
            return List.of();
        }
    }
}
