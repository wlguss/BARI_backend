package com.bari.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bari.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 특정 상품 재고 조회
    List<Inventory> findByProductIdAndDeletedAtIsNull(Long productId);

    // 전체 조회 (soft delete 제외)
    List<Inventory> findByDeletedAtIsNull();

    // 유통기한 지난 재고
    List<Inventory> findByExpireAtBeforeAndDeletedAtIsNull(LocalDateTime now);

    // 유통기한 임박 재고
    List<Inventory> findByExpireAtBetweenAndDeletedAtIsNull(
            LocalDateTime start,
            LocalDateTime end);

    // 존재 유무
    boolean existsByIdAndDeletedAtIsNull(Long id);

    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.deletedAt = :now
            WHERE i.expireAt < :now
            AND i.deletedAt IS NULL
            """)
    int bulkSoftDelete(@Param("now") LocalDateTime now);
}
