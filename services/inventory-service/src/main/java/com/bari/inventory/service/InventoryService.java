package com.bari.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.inventory.dto.request.InventoryKafkaRequest;
import com.bari.inventory.dto.request.InventoryRequest;
import com.bari.inventory.dto.request.InventoryUpdateRequest;
import com.bari.inventory.dto.request.RequestDiscount;
import com.bari.inventory.dto.response.InventoryResponse;
import com.bari.inventory.entity.Inventory;
import com.bari.inventory.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductFeignService productFeignService;

    // 재고 등록
    public InventoryResponse create(InventoryRequest dto) {

        try {
            productFeignService.getProduct(dto.getProductId());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("상품이 존재하지 않음");
        }
        Inventory inventory = dto.toEntity();
        Inventory saved = inventoryRepository.save(inventory);

        return InventoryResponse.fromEntity(saved);
    }

    // 특정 상품 재고 조회
    @Transactional(readOnly = true)
    public List<InventoryResponse> findByProduct(Long productId) {
        System.out.println("=== Service findByProduct parameter : " + productId);
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
    public void update(Long inventoryId, InventoryUpdateRequest dto) {

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new RuntimeException("재고 없음"));

        inventory.update(
                dto.getQuantity(),
                dto.getPrice(),
                dto.getExpireAt(),
                dto.getMemo());
    }

    // 재고 삭제 (soft delete)
    public void delete(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow();

        inventory.softDelete();
    }

    // 유통기한 만료 처리
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expireInventories() {
        int count = inventoryRepository.bulkSoftDelete(LocalDateTime.now());
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

    @KafkaListener(topics = "order.reserved")
    public void quantityConsumer(String message) {

        InventoryKafkaRequest request = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            request = objectMapper.readValue(message, InventoryKafkaRequest.class);
            Inventory inventory = inventoryRepository
                    .findById(request.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("상품이 존재하지 않음"));
            inventory.updateQuantity(request.getQuantity());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}