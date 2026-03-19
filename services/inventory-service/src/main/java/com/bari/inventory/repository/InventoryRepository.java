package com.bari.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bari.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    List<Inventory> findByProductIdAndDeletedAtIsNull(Long productId);

    List<Inventory> findByExpireAtBeforeAndDeletedAtIsNull(LocalDateTime now);

}
