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
import com.bari.discount.dto.client.StoreInfo;
import com.bari.discount.dto.request.DiscountRequest;
import com.bari.discount.dto.response.DiscountDetailResponse;
import com.bari.discount.dto.response.DiscountResponse;
import com.bari.discount.dto.response.ExpiringDiscountResponse;
import com.bari.discount.dto.response.StoreDiscountResponse;
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
    private final StoreFeignService storeFeignService;

    // RQ-4001 할인 등록
    public DiscountResponse create(DiscountRequest dto) {

        if (dto.getInventoryId() == null) {
            throw new BusinessException(DiscountErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            if (!Boolean.TRUE.equals(inventoryFeignService.existsInventory(dto.getInventoryId()).getData())) {
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
            if (!Boolean.TRUE.equals(inventoryFeignService.existsInventory(inventoryId).getData())) {
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
        List<ProductInfo> products = productFeignService.getProductsByStoreIds(storeIds).getData();
        if (products.isEmpty()) {
            return List.of();
        }

        // 2. productId → ProductInfo 맵 구성
        Map<Long, ProductInfo> productMap = products.stream()
                .collect(Collectors.toMap(ProductInfo::getId, p -> p));
        List<Long> productIds = List.copyOf(productMap.keySet());

        // 3. productIds → 재고 목록 조회
        List<InventoryInfo> inventories = inventoryFeignService.getInventoriesByProductIds(productIds).getData();
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
                .findByInventoryIdInAndEndAtBetweenAndDeletedAtIsNullOrderByCreatedAtDesc(inventoryIds, now, tomorrow);

        // 6. 응답 조합
        return discounts.stream()
                .map(d -> {
                    Long productId = inventoryToProductId.get(d.getInventoryId());
                    ProductInfo product = productMap.get(productId);
                    return ExpiringDiscountResponse.of(d, product);
                })
                .toList();
    }

    // RQ-4005 매장 기준 할인 전체 목록 조회
    @Transactional(readOnly = true)
    public List<StoreDiscountResponse> getDiscountsByStore(Long storeId) {
        // 1. storeId → 해당 매장의 상품 목록 조회
        List<ProductInfo> products = productFeignService.getProductsByStoreIds(List.of(storeId)).getData();
        if (products.isEmpty()) {
            return List.of();
        }

        // 2. productId → ProductInfo 맵 구성
        Map<Long, ProductInfo> productMap = products.stream()
                .collect(Collectors.toMap(ProductInfo::getId, p -> p));
        List<Long> productIds = List.copyOf(productMap.keySet());

        // 3. productIds → 재고 목록 조회
        List<InventoryInfo> inventories = inventoryFeignService.getInventoriesByProductIds(productIds).getData();
        if (inventories.isEmpty()) {
            return List.of();
        }

        // 4. inventoryId → productId 맵 구성
        Map<Long, Long> inventoryToProductId = inventories.stream()
                .collect(Collectors.toMap(InventoryInfo::getId, InventoryInfo::getProductId));
        List<Long> inventoryIds = List.copyOf(inventoryToProductId.keySet());

        // 5. inventoryIds로 활성 할인 전체 조회
        List<Discount> discounts = discountRepository.findByInventoryIdInAndDeletedAtIsNull(inventoryIds);

        // 6. 응답 조합: discount + imageUrl(상품)
        return discounts.stream()
                .map(d -> {
                    Long productId = inventoryToProductId.get(d.getInventoryId());
                    ProductInfo product = productMap.get(productId);
                    return StoreDiscountResponse.of(d, product);
                })
                .toList();
    }

    // 유저용 전체 할인 목록 조회
    @Transactional(readOnly = true)
    public List<StoreDiscountResponse> getAllDiscounts() {
        // 1. 전체 활성 할인 조회
        List<Discount> discounts = discountRepository.findAllByDeletedAtIsNull();
        if (discounts.isEmpty()) {
            return List.of();
        }

        // 2. inventoryId 목록 추출 → 재고 조회 (inventoryId → productId 매핑)
        List<Long> inventoryIds = discounts.stream()
                .map(Discount::getInventoryId)
                .distinct()
                .toList();
        List<InventoryInfo> inventories = inventoryFeignService.getInventoriesByIds(inventoryIds).getData();
        Map<Long, Long> inventoryToProductId = inventories.stream()
                .collect(Collectors.toMap(InventoryInfo::getId, InventoryInfo::getProductId));

        // 3. productId 목록 추출 → 상품 조회 (productId → ProductInfo 매핑)
        List<Long> productIds = inventoryToProductId.values().stream().distinct().toList();
        List<ProductInfo> products = productFeignService.getProductsByIds(productIds).getData();
        Map<Long, ProductInfo> productMap = products.stream()
                .collect(Collectors.toMap(ProductInfo::getId, p -> p));

        // 4. storeId 목록 추출 → 매장 조회 (storeId → StoreInfo 매핑)
        List<Long> storeIds = products.stream().map(ProductInfo::getStoreId).distinct().toList();
        List<StoreInfo> stores = storeFeignService.getStoresByIds(storeIds).getData();
        Map<Long, StoreInfo> storeMap = stores.stream()
                .collect(Collectors.toMap(StoreInfo::getId, s -> s));

        // 5. 응답 조합
        return discounts.stream()
                .filter(d -> inventoryToProductId.containsKey(d.getInventoryId()))
                .map(d -> {
                    Long productId = inventoryToProductId.get(d.getInventoryId());
                    ProductInfo product = productMap.get(productId);
                    StoreInfo store = product != null ? storeMap.get(product.getStoreId()) : null;
                    return StoreDiscountResponse.of(d, product, store);
                })
                .toList();
    }

    // 고객용 할인 상품 상세 조회
    @Transactional(readOnly = true)
    public DiscountDetailResponse getDiscountDetail(Long discountId) {
        // 1. 할인 조회
        Discount discount = discountRepository.findByIdAndDeletedAtIsNull(discountId)
                .orElseThrow(() -> new BusinessException(DiscountErrorCode.DISCOUNT_NOT_FOUND));

        // 2. 재고 조회
        List<InventoryInfo> inventories = inventoryFeignService.getInventoriesByIds(List.of(discount.getInventoryId())).getData();
        InventoryInfo inventory = inventories.stream().findFirst()
                .orElseThrow(() -> new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND));

        // 3. 상품 조회
        List<ProductInfo> products = productFeignService.getProductsByIds(List.of(inventory.getProductId())).getData();
        ProductInfo product = products.stream().findFirst()
                .orElseThrow(() -> new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND));

        // 4. 매장 조회
        List<StoreInfo> stores = storeFeignService.getStoresByIds(List.of(product.getStoreId())).getData();
        StoreInfo store = stores.stream().findFirst().orElse(null);

        return DiscountDetailResponse.of(discount, inventory, product, store);
    }

    // order-service용: 재고 ID 목록의 현재 활성 할인 조회
    @Transactional(readOnly = true)
    public List<DiscountResponse> getActiveDiscountsByInventoryIds(List<Long> inventoryIds) {
        return discountRepository.findActiveByInventoryIds(inventoryIds, LocalDateTime.now())
                .stream()
                .map(DiscountResponse::from)
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
