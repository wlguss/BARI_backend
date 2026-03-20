package com.bari.discount.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.discount.dto.request.DiscountRequest;
import com.bari.discount.dto.response.DiscountResponse;
import com.bari.discount.entity.Discount;
import com.bari.discount.repository.DiscountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountService {

    private final DiscountRepository discountRepository;

    // RQ-4001 할인 등록
    public DiscountResponse create(DiscountRequest dto) {

        Discount discount = dto.toEntity();
        Discount saved = discountRepository.save(discount);

        return DiscountResponse.from(saved);
    }

    // RQ-4002 할인 조회 (단건)
    @Transactional(readOnly = true)
    public DiscountResponse get(Long discountId) {

        Discount discount = discountRepository
                .findByIdAndDeletedAtIsNull(discountId)
                .orElseThrow();

        return DiscountResponse.from(discount);
    }

    // 목록 조회 (페이징)
    @Transactional(readOnly = true)
    public Page<DiscountResponse> getAll(Pageable pageable) {

        return discountRepository.findByDeletedAtIsNull(pageable)
                .map(DiscountResponse::from);
    }

    // RQ-4003 수정
    public DiscountResponse update(Long id, DiscountRequest dto) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow();

        discount.update(dto.getDiscountPrice());

        return DiscountResponse.from(discount);
    }

    // RQ-4004 종료 or 삭제
    public void delete(Long id) {

        Discount discount = discountRepository.findById(id)
                .orElseThrow();

        // 요구사항: end_at 또는 deleted_at
        discount.end(); // 종료 처리
        discount.delete(); // soft delete
    }
}
