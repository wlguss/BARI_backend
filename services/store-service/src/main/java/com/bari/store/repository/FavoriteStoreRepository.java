package com.bari.store.repository;

import com.bari.store.entity.FavoriteStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteStoreRepository extends JpaRepository<FavoriteStore, Long> {

    // 찜 여부 조회 (삭제 포함 — 토글 복구용)
    Optional<FavoriteStore> findByUserIdAndStoreId(Long userId, Long storeId);

    // 찜한 매장 목록 (활성 상태만)
    List<FavoriteStore> findByUserIdAndDeletedAtIsNull(Long userId);
}
