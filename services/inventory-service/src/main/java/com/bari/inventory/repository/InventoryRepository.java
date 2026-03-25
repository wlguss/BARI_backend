package com.bari.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bari.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // 매장 기준 재고 전체 조회 (products JOIN inventories)
    // 반환: [id, product_id, quantity, expire_at, name, description, image_url]
    @Query(value = """
            SELECT i.id, i.product_id, i.quantity, i.expire_at, i.created_at,
                   p.name, p.description, p.image_url
            FROM inventories i
            JOIN products p ON i.product_id = p.id
            WHERE p.store_id = :storeId
              AND i.deleted_at IS NULL
              AND p.deleted_at IS NULL
            """, nativeQuery = true)
    List<Object[]> findByStoreId(@Param("storeId") Long storeId);

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

    // 여러 productId로 한 번에 조회 (discount-service 내부 호출용)
    List<Inventory> findByProductIdInAndDeletedAtIsNull(List<Long> productIds);

    // 여러 inventoryId로 한 번에 조회 (전체 할인 목록용)
    List<Inventory> findByIdInAndDeletedAtIsNull(List<Long> ids);

    @Modifying
    @Query("""
            UPDATE Inventory i
            SET i.deletedAt = :now
            WHERE i.expireAt < :now
            AND i.deletedAt IS NULL
            """)
    int bulkSoftDelete(@Param("now") LocalDateTime now);
}
