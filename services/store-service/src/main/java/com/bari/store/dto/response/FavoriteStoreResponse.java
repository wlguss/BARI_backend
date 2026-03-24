package com.bari.store.dto.response;

import com.bari.store.entity.FavoriteStore;
import com.bari.store.entity.Store;
import lombok.Builder;
import lombok.Getter;

/**
 * 찜한 매장 응답 DTO.
 */
@Getter
@Builder
public class FavoriteStoreResponse {

    private Long favoriteId;
    private Long storeId;
    private String storeName;
    private String storeAddress;

    public static FavoriteStoreResponse of(FavoriteStore favorite, Store store) {
        return FavoriteStoreResponse.builder()
                .favoriteId(favorite.getId())
                .storeId(store.getId())
                .storeName(store.getName())
                .storeAddress(store.getAddress())
                .build();
    }
}
