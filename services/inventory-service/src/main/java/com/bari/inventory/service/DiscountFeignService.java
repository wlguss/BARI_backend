package com.bari.inventory.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.bari.inventory.dto.request.RequestDiscount;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@FeignClient(name = "discount-service", url = "http://localhost:8085")
public interface DiscountFeignService {

    @PostMapping("/api/store/discounts/internal")
    void createDiscount(@RequestBody RequestDiscount dto);
}
