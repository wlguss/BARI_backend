package com.bari.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.common.exception.BusinessException;
import com.bari.inventory.dto.request.InventoryKafkaRequest;
import com.bari.inventory.dto.request.InventoryRequest;
import com.bari.inventory.dto.request.InventoryUpdateRequest;
import com.bari.inventory.dto.request.RequestDiscount;
import com.bari.inventory.dto.response.InventoryResponse;
import com.bari.inventory.entity.Inventory;
import com.bari.inventory.exception.InventoryErrorCode;
import com.bari.inventory.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductFeignService productFeignService;

    // 재고 등록
    public InventoryResponse create(InventoryRequest dto) {

        if (dto.getProductId() == null || dto.getQuantity() == null) {
            throw new BusinessException(InventoryErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            productFeignService.getProduct(dto.getProductId());
        } catch (FeignException.NotFound e) {
            throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        } catch (FeignException e) {
            throw new BusinessException(InventoryErrorCode.INVENTORY_CREATE_FAILED);
        }

        if (dto.getQuantity() < 0) {
            throw new BusinessException(InventoryErrorCode.INVALID_QUANTITY);
        }

        // (선택) 0 허용 안 할 경우
        // if (dto.getQuantity() == 0) {
        // throw new BusinessException(InventoryErrorCode.INVALID_QUANTITY);
        // }

        try {
            Inventory inventory = dto.toEntity();
            Inventory saved = inventoryRepository.save(inventory);

            return InventoryResponse.fromEntity(saved);

        } catch (Exception e) {
            throw new BusinessException(InventoryErrorCode.INVENTORY_CREATE_FAILED);
        }
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

    public void update(Long inventoryId, InventoryUpdateRequest dto) {

        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new BusinessException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        // 삭제 여부 확인
        if (inventory.getDeletedAt() != null) {
            throw new BusinessException(InventoryErrorCode.INVENTORY_DELETED);
        }

        // 수량 검증
        if (dto.getQuantity() != null && dto.getQuantity() < 0) {
            throw new BusinessException(InventoryErrorCode.INVALID_QUANTITY);
        }

        try {
            inventory.update(
                    dto.getQuantity(),
                    dto.getPrice(),
                    dto.getExpireAt(),
                    dto.getMemo());
        } catch (Exception e) {
            throw new BusinessException(InventoryErrorCode.INVENTORY_UPDATE_FAILED);
        }
    }

    // 재고 삭제 (soft delete)
    public void delete(Long id) {

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(InventoryErrorCode.INVENTORY_NOT_FOUND));

        if (inventory.getDeletedAt() != null) {
            throw new BusinessException(InventoryErrorCode.INVENTORY_ALREADY_DELETED);
        }

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

    public boolean exists(Long inventoryId) {
        boolean exists = inventoryRepository.existsByIdAndDeletedAtIsNull(inventoryId);
        return exists;
    }

    @KafkaListener(topics = "order.reserved")
    public void quantityConsumer(String message) {

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InventoryKafkaRequest request = objectMapper.readValue(message, InventoryKafkaRequest.class);

            Inventory inventory = inventoryRepository
                    .findById(request.getInventoryId())
                    .orElseThrow(() -> new BusinessException(InventoryErrorCode.INVENTORY_NOT_FOUND));

            if (inventory.getDeletedAt() != null) {
                throw new BusinessException(InventoryErrorCode.INVENTORY_DELETED);
            }

            // 🔥 재고 부족 체크
            if (inventory.getQuantity() < request.getQuantity()) {
                throw new BusinessException(InventoryErrorCode.INSUFFICIENT_STOCK);
            }

            inventory.updateQuantity(request.getQuantity());

        } catch (BusinessException e) {
            // 비즈니스 에러는 로깅만
            log.warn("재고 처리 실패: {}", e.getMessage());
        } catch (Exception e) {
            // 시스템 에러
            log.error("Kafka 처리 중 오류", e);
        }
    }

}