package com.bari.store.service;

import com.bari.store.dto.request.StoreRequestDto; 
import com.bari.store.dto.response.StoreResponseDto;
import com.bari.store.entity.Store;
import com.bari.store.repository.StoreRepository;
import com.bari.user.entity.User;
import com.bari.user.repository.UserRepository;
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
    private final UserRepository userRepository;

    /**
     * 1. 매장 상세 조회
     */
    public StoreResponseDto getStoreDetail(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다. id=" + id));
        return StoreResponseDto.from(store);
    }

    /**
     * 2. 매장 전체 목록 조회 
     */
    public List<StoreResponseDto> getAllStores() {
        return storeRepository.findAll().stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 3. 매장 신규 등록
     */
    @Transactional 
    public Long createStore(Long userId, StoreRequestDto requestDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. id=" + userId));

        Store store = Store.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .businessHours(requestDto.getBusinessHours())
                .category(requestDto.getCategory())
                .imageUrl(requestDto.getImageUrl())
                .owner(owner)
                .build();
                
        return storeRepository.save(store).getId();
    }

    /**
     * 4. 매장 정보 수정
     * [수정 내용] 이미 삭제된(Soft Delete) 매장은 수정할 수 없도록 방어 로직 추가
     */
    @Transactional 
    public void updateStore(Long id, StoreRequestDto requestDto) { 
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 매장이 존재하지 않습니다. id=" + id));
        
                
        
        if (store.getDeletedAt() != null) {
            throw new IllegalArgumentException("이미 삭제된 매장은 수정할 수 없습니다.");
        }
                
        store.setName(requestDto.getName());
        store.setDescription(requestDto.getDescription());
        store.setAddress(requestDto.getAddress());
        store.setPhone(requestDto.getPhone());
        store.setBusinessHours(requestDto.getBusinessHours());
        store.setCategory(requestDto.getCategory());
        store.setImageUrl(requestDto.getImageUrl());
    }

    /**
     * [내부] 매장 단건 조회 (order-service 등 내부 서비스 통신용).
     * 삭제된 매장은 조회되지 않습니다.
     */
    public StoreResponseDto getStoreForInternal(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장이 존재하지 않습니다. id=" + storeId));
        if (store.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 매장입니다. id=" + storeId);
        }
        return StoreResponseDto.from(store);
    }

    /**
     * [내부] ownerId로 매장 조회 (order-service 매장 주문 조회용).
     * 사장님 userId로 소유 매장을 반환합니다.
     */
    public StoreResponseDto getStoreByOwnerId(Long ownerId) {
        Store store = storeRepository.findByOwner_Id(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 매장이 존재하지 않습니다. ownerId=" + ownerId));
        if (store.getDeletedAt() != null) {
            throw new IllegalArgumentException("삭제된 매장입니다. ownerId=" + ownerId);
        }
        return StoreResponseDto.from(store);
    }

    /**
     * 5. 매장 삭제 (Soft Delete 및 중복 삭제 예외 처리)
     */
    @Transactional
    public void deleteStore(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 매장이 존재하지 않습니다. id=" + id));
        
        // 이미 삭제된 매장인지 확인하는 로직 
        if (store.getDeletedAt() != null) {
            throw new IllegalArgumentException("이미 삭제된 매장입니다.");
        }

        store.delete(); 
    }
}