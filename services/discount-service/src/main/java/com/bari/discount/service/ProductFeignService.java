package com.bari.discount.service;

import com.bari.discount.config.FeignConfig;
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
    List<ProductInfo> getProductsByStoreIds(@RequestParam List<Long> storeIds);

    // 여러 productId로 상품 조회 (전체 할인 목록용)
    @GetMapping("/api/internal/products/by-ids")
    List<ProductInfo> getProductsByIds(@RequestParam List<Long> productIds);
}
