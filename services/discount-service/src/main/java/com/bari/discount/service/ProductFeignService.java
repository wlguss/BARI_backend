package com.bari.discount.service;

import com.bari.discount.config.FeignConfig;
import com.bari.discount.dto.client.ApiResponseWrapper;
import com.bari.discount.dto.client.ProductInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * product-service 내부 통신 Feign 클라이언트.
 * 찜한 매장의 할인 임박 상품 조회 시 storeIds → productIds 변환에 사용합니다.
 */
@FeignClient(name = "product-service", url = "${services.product.url}", configuration = FeignConfig.class)
public interface ProductFeignService {

    @GetMapping("/api/internal/products/by-stores")
    ApiResponseWrapper<List<ProductInfo>> getProductsByStoreIds(@RequestParam List<Long> storeIds);

    @GetMapping("/api/internal/products/by-ids")
    ApiResponseWrapper<List<ProductInfo>> getProductsByIds(@RequestParam List<Long> productIds);
}
