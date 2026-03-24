package com.bari.inventory.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bari.inventory.config.FeignConfig;
import com.bari.inventory.dto.response.ProductCheckResponse;

@FeignClient(name = "product-service", url = "http://${PRODUCT_SERVICE_HOST}:${PRODUCT_SERVICE_PORT}", configuration = FeignConfig.class)
public interface ProductFeignService {

    @GetMapping("/api/products/{productId}")
    ProductCheckResponse getProduct(@PathVariable("productId") Long productId);
}
