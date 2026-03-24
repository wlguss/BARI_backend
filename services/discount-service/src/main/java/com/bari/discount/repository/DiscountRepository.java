package com.bari.discount.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bari.discount.entity.Discount;

public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Page<Discount> findByDeletedAtIsNull(Pageable pageable);

    Optional<Discount> findByIdAndDeletedAtIsNull(Long id);

    List<Discount> findByInventoryIdAndDeletedAtIsNull(Long inventoryId);

    // 재고 ID 목록으로 활성 할인 전체 조회 (매장 기준 할인 목록용)
    List<Discount> findByInventoryIdInAndDeletedAtIsNull(List<Long> inventoryIds);

    // 찜한 매장 할인 임박 상품 조회 (endAt이 now~tomorrow 사이)
    List<Discount> findByInventoryIdInAndEndAtBetweenAndDeletedAtIsNull(
            List<Long> inventoryIds, LocalDateTime start, LocalDateTime end);
}
