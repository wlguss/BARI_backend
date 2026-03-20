package com.bari.discount.repository;

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
}
