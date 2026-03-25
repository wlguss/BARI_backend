package com.bari.discount.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bari.discount.entity.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Page<Discount> findByDeletedAtIsNull(Pageable pageable);

    List<Discount> findAllByDeletedAtIsNull();

    Optional<Discount> findByIdAndDeletedAtIsNull(Long id);

    List<Discount> findByInventoryIdAndDeletedAtIsNull(Long inventoryId);

    // 재고 ID 목록으로 활성 할인 전체 조회 (매장 기준 할인 목록용)
    List<Discount> findByInventoryIdInAndDeletedAtIsNull(List<Long> inventoryIds);

    // 찜한 매장 할인 임박 상품 조회 (endAt이 now~tomorrow 사이, 최신순)
    List<Discount> findByInventoryIdInAndEndAtBetweenAndDeletedAtIsNullOrderByCreatedAtDesc(
            List<Long> inventoryIds, LocalDateTime start, LocalDateTime end);

    // 재고 ID 목록의 현재 활성 할인 조회 (startAt <= now <= endAt, order-service 주문 가격 계산용)
    @Query("SELECT d FROM Discount d WHERE d.inventoryId IN :inventoryIds AND d.startAt <= :now AND d.endAt >= :now AND d.deletedAt IS NULL")
    List<Discount> findActiveByInventoryIds(@Param("inventoryIds") List<Long> inventoryIds, @Param("now") LocalDateTime now);
}
