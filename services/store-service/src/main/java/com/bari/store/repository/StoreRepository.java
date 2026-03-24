package com.bari.store.repository;

import com.bari.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findByIdAndDeletedAtIsNull(Long id);

    List<Store> findAllByDeletedAtIsNull();

    Optional<Store> findByOwnerIdAndDeletedAtIsNull(Long ownerId);

    Optional<Store> findByOwnerId(Long ownerId);
}
