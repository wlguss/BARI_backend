package com.bari.order.repository;

import com.bari.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /** 고객 주문 목록 조회 (삭제되지 않은 것만) */
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.deletedAt IS NULL")
    Page<Order> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    /** 매장 주문 목록 조회 (삭제되지 않은 것만) */
    @Query("SELECT o FROM Order o WHERE o.storeId = :storeId AND o.deletedAt IS NULL")
    Page<Order> findByStoreId(@Param("storeId") Long storeId, Pageable pageable);

    /** 단건 조회 (삭제되지 않은 것만) */
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.deletedAt IS NULL")
    Optional<Order> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
