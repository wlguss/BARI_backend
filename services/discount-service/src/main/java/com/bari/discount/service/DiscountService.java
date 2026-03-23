package com.bari.discount.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.common.exception.BusinessException;
import com.bari.discount.dto.request.DiscountRequest;
import com.bari.discount.dto.response.DiscountResponse;
import com.bari.discount.entity.Discount;
import com.bari.discount.exception.DiscountErrorCode;
import com.bari.discount.repository.DiscountRepository;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;

    private final InventoryFeignService inventoryFeignService;

    // RQ-4001 할인 등록
    public DiscountResponse create(DiscountRequest dto) {

        if (dto.getInventoryId() == null) {
            throw new BusinessException(DiscountErrorCode.INVALID_INPUT_VALUE);
        }

        try {
            if (!inventoryFeignService.existsInventory(dto.getInventoryId())) {
                throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
            }
        } catch (FeignException e) {
            throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
        }

        if (dto.getDiscountPrice() != null && dto.getDiscountPrice() < 0) {
            throw new BusinessException(DiscountErrorCode.INVALID_DISCOUNT_VALUE);
        }

        if (dto.getStartAt() != null && dto.getEndAt() != null) {
            if (dto.getStartAt().isAfter(dto.getEndAt())) {
                throw new BusinessException(DiscountErrorCode.INVALID_DATE_RANGE);
            }
        }

        try {
            Discount discount = dto.toEntity();
            Discount saved = discountRepository.save(discount);
            return DiscountResponse.from(saved);
        } catch (Exception e) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_CREATE_FAILED);
        }
    }

    // RQ-4002 할인 조회
    @Transactional(readOnly = true)
    public List<DiscountResponse> get(Long inventoryId) {

        // 재고 존재 검증
        try {
            if (!inventoryFeignService.existsInventory(inventoryId)) {
                throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
            }
        } catch (FeignException e) {
            throw new BusinessException(DiscountErrorCode.INVENTORY_NOT_FOUND);
        }

        List<Discount> discounts = discountRepository.findByInventoryIdAndDeletedAtIsNull(inventoryId);

        return discounts.stream()
                .map(DiscountResponse::from)
                .toList();
    }

    // RQ-4003 수정
    public DiscountResponse update(Long id, DiscountRequest dto) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(DiscountErrorCode.DISCOUNT_NOT_FOUND));

        if (discount.getDeletedAt() != null) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_DELETED);
        }

        // 할인값 검증
        if (dto.getDiscountPrice() != null && dto.getDiscountPrice() < 0) {
            throw new BusinessException(DiscountErrorCode.INVALID_DISCOUNT_VALUE);
        }

        // 날짜 검증
        if (dto.getStartAt() != null && dto.getEndAt() != null) {
            if (dto.getStartAt().isAfter(dto.getEndAt())) {
                throw new BusinessException(DiscountErrorCode.INVALID_DATE_RANGE);
            }
        }

        try {
            discount.update(dto.getDiscountPrice());
            return DiscountResponse.from(discount);
        } catch (Exception e) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_UPDATE_FAILED);
        }
    }

    // RQ-4004 종료 or 삭제
    public void delete(Long id) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(DiscountErrorCode.DISCOUNT_NOT_FOUND));

        if (discount.getDeletedAt() != null) {
            throw new BusinessException(DiscountErrorCode.DISCOUNT_ALREADY_DELETED);
        }

        discount.end(); // 종료 처리
        discount.softDelete(); // soft delete
    }
}
