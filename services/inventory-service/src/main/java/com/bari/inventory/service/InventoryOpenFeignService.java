package com.bari.inventory.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.bari.inventory.dto.response.InventoryResponse;

@FeignClient(name = "inventory-service")
public interface InventoryOpenFeignService {

    @GetMapping("/inventory/{inventoryId}")
    public InventoryResponse getInventoryId(@PathVariable("inventoryId") Long inventoryId,
            @RequestHeader("X-User-Id") String email);
}
