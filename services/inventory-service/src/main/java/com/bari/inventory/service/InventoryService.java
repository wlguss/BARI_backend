package com.bari.inventory.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.common.exception.BusinessException;
import com.bari.inventory.dto.request.InventoryKafkaRequest;
import com.bari.inventory.dto.request.InventoryRequest;
import com.bari.inventory.dto.request.InventoryUpdateRequest;
import com.bari.inventory.dto.response.InventoryResponse;
import com.bari.inventory.dto.response.StoreInventoryResponse;
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
            System.out.println("==== Feign 호출 시작: productId=" + dto.getProductId());
            productFeignService.getProduct(dto.getProductId());
            System.out.println("==== Feign 호출 성공");
        } catch (FeignException.NotFound e) {
            e.printStackTrace();
            throw new BusinessException(InventoryErrorCode.PRODUCT_NOT_FOUND);
        } catch (FeignException e) {
            e.printStackTrace();
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
        List<InventoryResponse> response = inventoryRepository.findByProductIdAndDeletedAtIsNull(productId)
                .stream()
                .map(InventoryResponse::fromEntity)
                .toList();

        System.out.println("==== inven service : " + response);
        return response;
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
        inventoryRepository.bulkSoftDelete(LocalDateTime.now());
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

    // 여러 productId로 재고 목록 조회 (discount-service 내부 호출용)
    @Transactional(readOnly = true)
    public List<InventoryResponse> findByProductIds(List<Long> productIds) {
        return inventoryRepository.findByProductIdInAndDeletedAtIsNull(productIds)
                .stream()
                .map(InventoryResponse::fromEntity)
                .toList();
    }

    // 여러 inventoryId로 재고 목록 조회 (전체 할인 목록용)
    @Transactional(readOnly = true)
    public List<InventoryResponse> findByIds(List<Long> ids) {
        return inventoryRepository.findByIdInAndDeletedAtIsNull(ids)
                .stream()
                .map(InventoryResponse::fromEntity)
                .toList();
    }

    // 특정 매장의 재고 전체 조회 (products JOIN inventories)
    @Transactional(readOnly = true)
    public List<StoreInventoryResponse> findByStore(Long storeId) {
        return inventoryRepository.findByStoreId(storeId)
                .stream()
                .map(StoreInventoryResponse::fromRow)
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

            // 유통기한 임박 순으로 정렬 후 FIFO 차감
            List<Inventory> inventories = inventoryRepository
                    .findByProductIdAndDeletedAtIsNull(request.getProductId())
                    .stream()
                    .sorted(Comparator.comparing(Inventory::getExpireAt,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();

            int totalStock = inventories.stream().mapToInt(Inventory::getQuantity).sum();
            if (totalStock < request.getQuantity()) {
                log.warn("재고 부족 - productId: {}, 요청 수량: {}, 현재 재고: {}",
                        request.getProductId(), request.getQuantity(), totalStock);
                return;
            }

            int remaining = request.getQuantity();
            for (Inventory inv : inventories) {
                if (remaining <= 0) break;
                int deduct = Math.min(inv.getQuantity(), remaining);
                inv.updateQuantity(deduct);
                remaining -= deduct;
            }

            log.info("재고 차감 완료 - productId: {}, 차감 수량: {}", request.getProductId(), request.getQuantity());
        } catch (Exception e) {
            log.error("Kafka order.reserved 처리 중 오류", e);
        }
    }

    @KafkaListener(topics = "order.cancelled")
    public void cancelConsumer(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            InventoryKafkaRequest request = objectMapper.readValue(message, InventoryKafkaRequest.class);

            // 차감 시 가장 먼저 건드린 재고(유통기한 임박)에 복구
            List<Inventory> inventories = inventoryRepository
                    .findByProductIdAndDeletedAtIsNull(request.getProductId())
                    .stream()
                    .sorted(Comparator.comparing(Inventory::getExpireAt,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();

            if (inventories.isEmpty()) {
                log.warn("재고 복구 대상 없음 - productId: {}", request.getProductId());
                return;
            }

            inventories.get(0).restoreQuantity(request.getQuantity());
            log.info("재고 복구 완료 - productId: {}, 복구 수량: {}", request.getProductId(), request.getQuantity());
        } catch (Exception e) {
            log.error("Kafka order.cancelled 처리 중 오류", e);
        }
    }

}