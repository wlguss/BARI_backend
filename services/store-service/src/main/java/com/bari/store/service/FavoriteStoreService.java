package com.bari.store.service;

import com.bari.common.exception.BusinessException;
import com.bari.store.client.DiscountServiceClient;
import com.bari.store.dto.client.ExpiringDiscountInfo;
import com.bari.store.dto.response.FavoriteStoreResponse;
import com.bari.store.entity.FavoriteStore;
import com.bari.store.entity.Store;
import com.bari.store.exception.StoreErrorCode;
import com.bari.store.repository.FavoriteStoreRepository;
import com.bari.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteStoreService {

    private final FavoriteStoreRepository favoriteStoreRepository;
    private final StoreRepository storeRepository;
    private final DiscountServiceClient discountServiceClient;

    /**
     * 찜하기 / 찜해제 토글.
     * - 기존 기록 없음: 찜하기 (신규 생성)
     * - 기존 기록 있고 deletedAt null: 찜해제 (soft delete)
     * - 기존 기록 있고 deletedAt 존재: 다시 찜하기 (복구)
     */
    @Transactional
    public void toggleFavorite(Long userId, Long storeId) {
        // 매장 존재 확인
        storeRepository.findById(storeId)
                .filter(s -> s.getDeletedAt() == null)
                .orElseThrow(() -> new BusinessException(StoreErrorCode.STORE_NOT_FOUND));

        Optional<FavoriteStore> existing = favoriteStoreRepository.findByUserIdAndStoreId(userId, storeId);

        if (existing.isEmpty()) {
            // 최초 찜하기
            favoriteStoreRepository.save(FavoriteStore.of(userId, storeId));
        } else {
            FavoriteStore favorite = existing.get();
            if (favorite.isDeleted()) {
                // 찜해제 후 다시 찜하기 → 복구
                favorite.restore();
            } else {
                // 찜해제
                favorite.softDelete();
            }
        }
    }

    /**
     * 찜한 매장 목록 조회.
     */
    public List<FavoriteStoreResponse> getFavoriteStores(Long userId) {
        List<FavoriteStore> favorites = favoriteStoreRepository.findByUserIdAndDeletedAtIsNull(userId);

        List<Long> storeIds = favorites.stream()
                .map(FavoriteStore::getStoreId)
                .toList();

        if (storeIds.isEmpty()) {
            return List.of();
        }

        // N+1 방지: storeIds로 한 번에 조회
        List<Store> stores = storeRepository.findAllById(storeIds);

        return favorites.stream()
                .flatMap(fav -> stores.stream()
                        .filter(s -> s.getId().equals(fav.getStoreId()))
                        .map(store -> FavoriteStoreResponse.of(fav, store)))
                .toList();
    }

    /**
     * 찜한 매장의 할인 임박 상품 목록 조회 (홈화면용).
     * discount-service에서 내일 마감까지 등록된 상품만 반환합니다.
     */
    public List<ExpiringDiscountInfo> getExpiringDiscounts(Long userId) {
        List<Long> storeIds = favoriteStoreRepository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(FavoriteStore::getStoreId)
                .toList();

        if (storeIds.isEmpty()) {
            return List.of();
        }

        return discountServiceClient.getExpiringDiscounts(storeIds, userId);
    }
}
