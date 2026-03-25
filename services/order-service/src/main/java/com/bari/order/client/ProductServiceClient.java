package com.bari.order.client;

import com.bari.common.exception.BusinessException;
import com.bari.order.dto.client.ProductInfo;
import com.bari.order.exception.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * product-service 내부 API 클라이언트.
 * order-service에서 상품 존재 여부 검증 시 사용합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestClient productRestClient;

    /**
     * 상품 존재 여부 검증.
     * product-service: GET /api/internal/products/{productId}
     *
     * @param productId 검증할 상품 ID
     * @return 상품 정보
     * @throws BusinessException PRODUCT_NOT_FOUND — 존재하지 않거나 삭제된 상품
     */
    public ProductInfo getProduct(Long productId) {
        log.debug("product-service 상품 조회 요청 - productId: {}", productId);

        return productRestClient.get()
                .uri("/api/internal/products/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.warn("product-service 상품 조회 실패 - productId: {}, status: {}", productId, response.getStatusCode());
                    throw new BusinessException(OrderErrorCode.PRODUCT_NOT_FOUND);
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("product-service 서버 오류 - productId: {}, status: {}", productId, response.getStatusCode());
                    throw new BusinessException(OrderErrorCode.PRODUCT_SERVICE_UNAVAILABLE);
                })
                .body(new ParameterizedTypeReference<ApiResponseWrapper<ProductInfo>>() {})
                .getData();
    }
}
