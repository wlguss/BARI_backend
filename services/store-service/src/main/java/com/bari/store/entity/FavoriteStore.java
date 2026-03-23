package com.bari.store.entity;

import com.bari.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 찜한 매장 엔티티.
 * soft delete 기반 토글: deletedAt null = 찜 상태, null이 아니면 찜 해제 상태.
 */
@Entity
@Table(name = "favorite_stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteStore extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long storeId;

    public static FavoriteStore of(Long userId, Long storeId) {
        FavoriteStore favoriteStore = new FavoriteStore();
        favoriteStore.userId = userId;
        favoriteStore.storeId = storeId;
        return favoriteStore;
    }
}
