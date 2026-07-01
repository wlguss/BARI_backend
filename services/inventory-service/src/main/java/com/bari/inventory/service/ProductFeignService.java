package com.bari.inventory.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bari.inventory.config.FeignConfig;
import com.bari.inventory.dto.client.ApiResponseWrapper;
import com.bari.inventory.dto.response.ProductCheckResponse;
import com.bari.inventory.dto.response.StoreProductResponse;

@FeignClient(name = "product-service", url = "${services.product.url}", configuration = FeignConfig.class)
public interface ProductFeignService {

    @GetMapping("/api/products/{productId}")
    ApiResponseWrapper<ProductCheckResponse> getProduct(@PathVariable Long productId);

    @GetMapping("/api/products")
    ApiResponseWrapper<List<StoreProductResponse>> getProductsByStoreId(@RequestParam Long storeId);
}
