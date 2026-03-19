package com.bari.inventory.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bari.inventory.dto.request.InventoryRequest;
import com.bari.inventory.dto.request.RequestDiscount;
import com.bari.inventory.dto.response.InventoryResponse;
import com.bari.inventory.entity.Inventory;
import com.bari.inventory.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final DiscountFeignService discountClient;

    public InventoryResponse create(InventoryRequest dto) {

        Inventory inventory = dto.toEntity();

        Inventory saved = inventoryRepository.save(inventory);

        RequestDiscount discountDto = RequestDiscount.builder()
                .inventoryId(saved.getId())
                .originalPrice(dto.getOriginalPrice())
                .discountRate(dto.getDiscountRate())
                .build();

        discountClient.createDiscount(discountDto);

        return InventoryResponse.fromEntity(saved);
    }

    public List<InventoryResponse> findByProduct(Long productId) {
        return inventoryRepository.findByProductIdAndDeletedAtIsNull(productId)
                .stream()
                .map(InventoryResponse::fromEntity)
                .toList();
    }

    public void update(Long id, Integer quantity, LocalDateTime expireAt) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow();

        inventory.update(quantity, expireAt);
    }

    public void delete(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow();

        inventory.delete();
    }

}
