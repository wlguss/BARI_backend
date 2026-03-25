package com.bari.order.repository;

import com.bari.order.entity.Order;
import com.bari.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /** 고객 주문 목록 조회 (삭제되지 않은 것만) */
    @Query("SELECT o FROM Order o WHERE o.customerId = :customerId AND o.deletedAt IS NULL")
    Page<Order> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    /** 매장 주문 목록 조회 (삭제되지 않은 것만, 페이지네이션용) */
    @Query("SELECT o FROM Order o WHERE o.storeId = :storeId AND o.deletedAt IS NULL")
    Page<Order> findByStoreId(@Param("storeId") Long storeId, Pageable pageable);

    /** 매장 전체 주문 목록 조회 — status 필터 없음 */
    @Query("SELECT o FROM Order o WHERE o.storeId = :storeId AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    List<Order> findAllByStoreId(@Param("storeId") Long storeId);

    /** 매장 전체 주문 목록 조회 — status 필터 적용 */
    @Query("SELECT o FROM Order o WHERE o.storeId = :storeId AND o.status = :status AND o.deletedAt IS NULL ORDER BY o.createdAt DESC")
    List<Order> findAllByStoreIdAndStatus(@Param("storeId") Long storeId, @Param("status") OrderStatus status);

    /** 단건 조회 (삭제되지 않은 것만) */
    @Query("SELECT o FROM Order o WHERE o.id = :id AND o.deletedAt IS NULL")
    Optional<Order> findByIdAndDeletedAtIsNull(@Param("id") Long id);
}
