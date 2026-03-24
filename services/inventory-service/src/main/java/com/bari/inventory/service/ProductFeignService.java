package com.bari.inventory.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bari.inventory.config.FeignConfig;
import com.bari.inventory.dto.response.ProductCheckResponse;
import com.bari.inventory.dto.response.StoreProductResponse;

@FeignClient(name = "product-service", url = "${services.product.url}", configuration = FeignConfig.class)
public interface ProductFeignService {

    @GetMapping("/api/products/{productId}")
    ProductCheckResponse getProduct(@PathVariable Long productId);

    // 특정 매장의 상품 목록 조회 (매장 재고 전체 조회용)
    @GetMapping("/api/products")
    List<StoreProductResponse> getProductsByStoreId(@RequestParam Long storeId);
}
