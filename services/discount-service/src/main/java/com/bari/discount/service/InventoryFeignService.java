package com.bari.discount.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.bari.discount.config.FeignConfig;

@FeignClient(name = "inventory-service", url = "http://localhost:8084", configuration = FeignConfig.class)
public interface InventoryFeignService {

    @GetMapping("/api/store/inventory/exists/{inventoryId}")
    boolean existsInventory(@PathVariable Long inventoryId);
}
