package com.bari.store.service;

import com.bari.store.dto.request.StoreRequestDto; 
import com.bari.store.dto.response.StoreResponseDto;
import com.bari.store.entity.Store;
import com.bari.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {
    private final StoreRepository storeRepository;

    public StoreResponseDto getStoreDetail(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다. id=" + id));
        return StoreResponseDto.from(store);
    }

    public List<StoreResponseDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional 
    public Long createStore(StoreRequestDto requestDto) {
        Store store = Store.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .businessHours(requestDto.getBusinessHours())
                .category(requestDto.getCategory())
                .imageUrl(requestDto.getImageUrl())
                .ownerId(1L) 
                .build();
        return storeRepository.save(store).getId();
    }

    @Transactional 
    public void updateStore(Long id, StoreRequestDto requestDto) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 매장이 존재하지 않습니다. id=" + id));
                
        store.setName(requestDto.getName()); // 엔티티의 name 필드 수정
        store.setDescription(requestDto.getDescription());
        store.setAddress(requestDto.getAddress());
        store.setPhone(requestDto.getPhone());
        store.setBusinessHours(requestDto.getBusinessHours());
        store.setCategory(requestDto.getCategory());
        store.setImageUrl(requestDto.getImageUrl());
    }

    @Transactional
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 매장이 존재하지 않습니다. id=" + id));
        store.setDeletedAt(java.time.LocalDateTime.now());
    }
}