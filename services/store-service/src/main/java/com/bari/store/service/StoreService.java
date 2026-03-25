package com.bari.store.service;

import com.bari.common.exception.BusinessException;
import com.bari.store.dto.request.StoreRequestDto;
import com.bari.store.dto.response.StoreResponseDto;
import com.bari.store.entity.Store;
import com.bari.store.exception.StoreErrorCode;
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
     * 매장 상세 조회.
     */
    public StoreResponseDto getStoreDetail(Long id) {
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(StoreErrorCode.STORE_NOT_FOUND));
        return StoreResponseDto.from(store);
    }

    /**
     * 매장 전체 목록 조회.
     */
    public List<StoreResponseDto> getAllStores() {
        return storeRepository.findAllByDeletedAtIsNull().stream()
                .map(StoreResponseDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 매장 신규 등록.
     */
    @Transactional
    public Long createStore(Long userId, StoreRequestDto requestDto) {
        Store store = Store.builder()
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .address(requestDto.getAddress())
                .phone(requestDto.getPhone())
                .businessHours(requestDto.getBusinessHours())
                .category(requestDto.getCategory())
                .imageUrl(requestDto.getImageUrl())
                .ownerId(userId)
                .build();
        return storeRepository.save(store).getId();
    }

    /**
     * 매장 정보 수정.
     */
    @Transactional
    public void updateStore(Long id, StoreRequestDto requestDto) {
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(StoreErrorCode.STORE_NOT_FOUND));
        store.updateInfo(
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getAddress(),
                requestDto.getPhone(),
                requestDto.getBusinessHours(),
                requestDto.getCategory(),
                requestDto.getImageUrl()
        );
    }

    /**
     * 매장 삭제 (Soft Delete).
     */
    @Transactional
    public void deleteStore(Long id) {
        Store store = storeRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(StoreErrorCode.STORE_NOT_FOUND));
        store.delete();
    }

    /**
     * [내부] 매장 단건 조회 (order-service 등 내부 서비스 통신용).
     */
    public StoreResponseDto getStoreForInternal(Long storeId) {
        Store store = storeRepository.findByIdAndDeletedAtIsNull(storeId)
                .orElseThrow(() -> new BusinessException(StoreErrorCode.STORE_NOT_FOUND));
        return StoreResponseDto.from(store);
    }

    /**
     * [내부] ownerId로 매장 조회 (order-service 매장 주문 조회용).
     */
    public StoreResponseDto getStoreByOwnerId(Long ownerId) {
        Store store = storeRepository.findByOwnerIdAndDeletedAtIsNull(ownerId)
                .orElseThrow(() -> new BusinessException(StoreErrorCode.STORE_NOT_FOUND));
        return StoreResponseDto.from(store);
    }

    /**
     * [내부] storeId 목록으로 매장 일괄 조회 (discount-service 전체 할인 목록 조회용).
     */
    public List<StoreResponseDto> getStoresByIds(List<Long> storeIds) {
        return storeRepository.findAllByIdInAndDeletedAtIsNull(storeIds).stream()
                .map(StoreResponseDto::from)
                .toList();
    }
}
