package com.bari.discount.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.common.exception.BusinessException;
import com.bari.discount.dto.client.InventoryInfo;
import com.bari.discount.dto.client.ProductInfo;
import com.bari.discount.dto.request.DiscountRequest;
import com.bari.discount.dto.response.DiscountResponse;
import com.bari.discount.dto.response.ExpiringDiscountResponse;
import com.bari.discount.entity.Discount;
import com.bari.discount.exception.DiscountErrorCode;
import com.bari.discount.repository.DiscountRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;
    private final InventoryFeignService inventoryFeignService;
    private final ProductFeignService productFeignService;

    // RQ-4001 할인 등록
    public DiscountResponse create(DiscountRequest dto) {

        if (dto.getInventoryId() == null) {
            throw new BusinessException(DiscountErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            if (!inventoryFeignService.existsInventory(dto.getInventoryId())) {
                throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
            }
        } catch (FeignException e) {
            throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
        }

        if (dto.getDiscountPrice() != null && dto.getDiscountPrice() < 0) {
            throw new BusinessException(DiscountErrorCode.INVALID_DISCOUNT_VALUE);
        }

        if (dto.getStartAt() != null && dto.getEndAt() != null) {
            if (dto.getStartAt().isAfter(dto.getEndAt())) {
                throw new BusinessException(DiscountErrorCode.INVALID_DATE_RANGE);
            }
        }

        try {
            Discount discount = dto.toEntity();
            Discount saved = discountRepository.save(discount);
            return DiscountResponse.from(saved);
        } catch (Exception e) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_CREATE_FAILED);
        }
    }

    // RQ-4002 할인 조회
    @Transactional(readOnly = true)
    public List<DiscountResponse> get(Long inventoryId) {

        // 재고 존재 검증
        try {
            if (!inventoryFeignService.existsInventory(inventoryId)) {
                throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
            }
        } catch (FeignException e) {
            throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
        }

        List<Discount> discounts = discountRepository.findByInventoryIdAndDeletedAtIsNull(inventoryId);

        return discounts.stream()
                .map(DiscountResponse::from)
                .toList();
    }

    // RQ-4003 수정
    public DiscountResponse update(Long id, DiscountRequest dto) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(DiscountErrorCode.DISCOUNT_NOT_FOUND));

        if (discount.getDeletedAt() != null) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_DELETED);
        }

        // 할인값 검증
        if (dto.getDiscountPrice() != null && dto.getDiscountPrice() < 0) {
            throw new BusinessException(DiscountErrorCode.INVALID_DISCOUNT_VALUE);
        }

        // 날짜 검증
        if (dto.getStartAt() != null && dto.getEndAt() != null) {
            if (dto.getStartAt().isAfter(dto.getEndAt())) {
                throw new BusinessException(DiscountErrorCode.INVALID_DATE_RANGE);
            }
        }

        try {
            discount.update(dto.getDiscountPrice());
            return DiscountResponse.from(discount);
        } catch (Exception e) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_UPDATE_FAILED);
        }
    }

    // 찜한 매장의 할인 임박 상품 조회 (내일 마감 기준, store-service 내부 호출용)
    @Transactional(readOnly = true)
    public List<ExpiringDiscountResponse> getExpiringDiscountsByStoreIds(List<Long> storeIds) {
        // 1. storeIds → 상품 목록 조회
        List<ProductInfo> products = productFeignService.getProductsByStoreIds(storeIds);
        if (products.isEmpty()) {
            return List.of();
        }

        // 2. productId → ProductInfo 맵 구성
        Map<Long, ProductInfo> productMap = products.stream()
                .collect(Collectors.toMap(ProductInfo::getId, p -> p));
        List<Long> productIds = List.copyOf(productMap.keySet());

        // 3. productIds → 재고 목록 조회
        List<InventoryInfo> inventories = inventoryFeignService.getInventoriesByProductIds(productIds);
        if (inventories.isEmpty()) {
            return List.of();
        }

        // 4. inventoryId → productId 맵 구성
        Map<Long, Long> inventoryToProductId = inventories.stream()
                .collect(Collectors.toMap(InventoryInfo::getId, InventoryInfo::getProductId));
        List<Long> inventoryIds = List.copyOf(inventoryToProductId.keySet());

        // 5. 내일 마감 할인 조회
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1).toLocalDate().atTime(23, 59, 59);
        List<Discount> discounts = discountRepository
                .findByInventoryIdInAndEndAtBetweenAndDeletedAtIsNull(inventoryIds, now, tomorrow);

        // 6. 응답 조합
        return discounts.stream()
                .map(d -> {
                    Long productId = inventoryToProductId.get(d.getInventoryId());
                    ProductInfo product = productMap.get(productId);
                    return ExpiringDiscountResponse.of(d, product);
                })
                .toList();
    }

    // RQ-4004 종료 or 삭제
    public void delete(Long id) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(DiscountErrorCode.DISCOUNT_NOT_FOUND));

        if (discount.getDeletedAt() != null) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_ALREADY_DELETED);
        }

        discount.end(); // 종료 처리
        discount.softDelete(); // soft delete
    }
}
