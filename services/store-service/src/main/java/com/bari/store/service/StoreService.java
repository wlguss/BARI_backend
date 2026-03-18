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

    /**
     * 특정 매장 상세 조회
     */
    public StoreResponseDto getStoreDetail(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다. id=" + id));
        return StoreResponseDto.from(store);
    }

    /**
     * 전체 매장 목록 조회
     */
    public List<StoreResponseDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 1. 매장 신규 등록
     */
    @Transactional 
    public Long createStore(StoreRequestDto requestDto) {
        Store store = Store.builder()
                .storeName(requestDto.getStoreName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .businessHours(requestDto.getBusinessHours())
                .category(requestDto.getCategory())
                .imageUrl(requestDto.getImageUrl())
                // TODO: 추후 로그인 정보가 완성되면 실제 유저 ID로 변경 예정
                .ownerId(1L) 
                .build();

        return storeRepository.save(store).getId();
    }

    /**
     * 2. 매장 정보 수정 기능
     */
    @Transactional 
    public void updateStore(Long id, StoreRequestDto requestDto) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 매장이 존재하지 않습니다. id=" + id));
        
        store.setStoreName(requestDto.getStoreName());
        store.setDescription(requestDto.getDescription());
        store.setAddress(requestDto.getAddress());
        store.setPhone(requestDto.getPhone());
        store.setBusinessHours(requestDto.getBusinessHours());
        store.setCategory(requestDto.getCategory());
        store.setImageUrl(requestDto.getImageUrl());
    }
}