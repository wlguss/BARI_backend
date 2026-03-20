package com.bari.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.inventory.dto.request.InventoryRequest;
import com.bari.inventory.dto.request.RequestDiscount;
import com.bari.inventory.dto.response.InventoryResponse;
import com.bari.inventory.entity.Inventory;
import com.bari.inventory.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final DiscountFeignService discountClient;

    // 재고 등록
    public InventoryResponse create(InventoryRequest dto) {

        Inventory inventory = dto.toEntity();
        Inventory saved = inventoryRepository.save(inventory);

        RequestDiscount discountDto = RequestDiscount.builder()
                .inventoryId(saved.getId())
                .originalPrice(dto.getOriginalPrice())
                .discountPrice(dto.getDiscountPrice())
                .build();

        discountClient.createDiscount(discountDto);

        return InventoryResponse.fromEntity(saved);
    }

    // 특정 상품 재고 조회
    @Transactional(readOnly = true)
    public List<InventoryResponse> findByProduct(Long productId) {
        return inventoryRepository.findByProductIdAndDeletedAtIsNull(productId)
                .stream()
                .map(InventoryResponse::fromEntity)
                .toList();
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<InventoryResponse> findAll() {
        return inventoryRepository.findByDeletedAtIsNull()
                .stream()
                .map(InventoryResponse::fromEntity)
                .toList();
    }

    // 재고 수정
    public void update(Long id, Integer quantity, LocalDateTime expireAt) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow();

        inventory.update(quantity, expireAt);
    }

    // 재고 삭제 (soft delete)
    public void delete(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow();

        inventory.delete();
    }

    // 유통기한 만료 처리
    public void expireInventories() {
        List<Inventory> inventories = inventoryRepository.findByExpireAtBeforeAndDeletedAtIsNull(LocalDateTime.now());

        inventories.forEach(Inventory::isExpired);
    }

    // 유통기한 임박 조회
    @Transactional(readOnly = true)
    public List<InventoryResponse> findNearExpire() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusDays(3); // 기준: 3일

        return inventoryRepository
                .findByExpireAtBetweenAndDeletedAtIsNull(now, threshold)
                .stream()
                .map(InventoryResponse::fromEntity)
                .toList();
    }
}
