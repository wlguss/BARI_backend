package com.bari.discount.service;

import com.bari.discount.config.FeignConfig;
import com.bari.discount.dto.client.StoreInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * store-service 내부 통신 Feign 클라이언트.
 * 전체 할인 목록 조회 시 storeIds → storeName 변환에 사용합니다.
 */
@FeignClient(name = "store-service", url = "${services.store.url}", configuration = FeignConfig.class)
public interface StoreFeignService {

    @GetMapping("/api/internal/stores/by-ids")
    List<StoreInfo> getStoresByIds(@RequestParam List<Long> storeIds);
}
