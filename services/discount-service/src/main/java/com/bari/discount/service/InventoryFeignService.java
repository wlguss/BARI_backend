package com.bari.discount.service;

import com.bari.discount.config.FeignConfig;
import com.bari.discount.dto.client.ApiResponseWrapper;
import com.bari.discount.dto.client.InventoryInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "inventory-service", url = "${services.inventory.url}", configuration = FeignConfig.class)
public interface InventoryFeignService {

    @GetMapping("/api/store/inventory/exists/{inventoryId}")
    ApiResponseWrapper<Boolean> existsInventory(@PathVariable Long inventoryId);

    @GetMapping("/api/internal/inventories/by-products")
    ApiResponseWrapper<List<InventoryInfo>> getInventoriesByProductIds(@RequestParam List<Long> productIds);

    @GetMapping("/api/internal/inventories/by-ids")
    ApiResponseWrapper<List<InventoryInfo>> getInventoriesByIds(@RequestParam List<Long> inventoryIds);
}
