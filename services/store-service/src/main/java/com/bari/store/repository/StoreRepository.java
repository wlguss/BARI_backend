package com.bari.store.repository;

import com.bari.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    // 저장(save), 조회(findById), 삭제(delete) 기능

    /** ownerId로 매장 조회 (내부 서비스 통신용) */
    Optional<Store> findByOwner_Id(Long ownerId);
}