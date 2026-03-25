package com.bari.discount.service;

import com.bari.discount.config.FeignConfig;
import com.bari.discount.dto.client.InventoryInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "inventory-service", url = "${services.inventory.url}", configuration = FeignConfig.class)
public interface InventoryFeignService {

    @GetMapping("/api/store/inventory/exists/{inventoryId}")
    boolean existsInventory(@PathVariable Long inventoryId);

    // 여러 productId로 재고 목록 조회 (찜한 매장 할인 임박 상품용)
    @GetMapping("/api/internal/inventories/by-products")
    List<InventoryInfo> getInventoriesByProductIds(@RequestParam List<Long> productIds);

    // 여러 inventoryId로 재고 조회 (전체 할인 목록용)
    @GetMapping("/api/internal/inventories/by-ids")
    List<InventoryInfo> getInventoriesByIds(@RequestParam List<Long> inventoryIds);
}
