package com.bari.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bari.product.entity.ProductEntity;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long>{
    Optional<ProductEntity> findByIdAndDeletedAtIsNull(Long id);

    List<ProductEntity> findAllByDeletedAtIsNullOrderByIdDesc();

    List<ProductEntity> findAllByStoreIdAndDeletedAtIsNullOrderByIdDesc(Long storeId);

    List<ProductEntity> findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrderByIdDesc(String name);

    List<ProductEntity> findAllByStoreIdAndNameContainingIgnoreCaseAndDeletedAtIsNullOrderByIdDesc(Long storeId, String name);
}
