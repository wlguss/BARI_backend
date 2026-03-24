package com.bari.store.client;

import com.bari.common.exception.BusinessException;
import com.bari.store.dto.client.ExpiringDiscountInfo;
import com.bari.store.exception.StoreErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * discount-service 내부 통신 클라이언트.
 */
@Component
@RequiredArgsConstructor
public class DiscountServiceClient {

    private final RestClient discountRestClient;

    /**
     * 찜한 매장의 할인 임박 상품 목록 조회.
     *
     * @param storeIds 찜한 매장 ID 목록
     * @param userId   요청 사용자 ID
     * @return 할인 임박 상품 목록
     */
    public List<ExpiringDiscountInfo> getExpiringDiscounts(List<Long> storeIds, Long userId) {
        String storeIdsParam = storeIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        try {
            return discountRestClient.get()
                    .uri("/api/internal/discounts/expiring?storeIds=" + storeIdsParam)
                    .header("X-User-Id", String.valueOf(userId))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException e) {
            throw new BusinessException(StoreErrorCode.DISCOUNT_SERVICE_UNAVAILABLE);
        }
    }
}
