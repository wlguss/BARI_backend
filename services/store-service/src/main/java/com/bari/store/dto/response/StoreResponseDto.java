package com.bari.store.dto.response;

import com.bari.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreResponseDto {
    private Long id;
    private String storeName;
    private String description;
    private String address;
    private String phone;
    private String businessHours;
    private String category;
    private String imageUrl;
    private Long ownerId;

    
    public static StoreResponseDto from(Store store) {
        return StoreResponseDto.builder()
                .id(store.getId())
                .storeName(store.getName())
                .description(store.getDescription())
                .address(store.getAddress())
                .phone(store.getPhone())
                .businessHours(store.getBusinessHours())
                .category(store.getCategory())
                .imageUrl(store.getImageUrl())
                .ownerId(store.getOwner() != null ? store.getOwner().getId() : null)
                .build();
    }
}