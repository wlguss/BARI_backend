package com.bari.discount.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bari.discount.dto.request.DiscountRequest;
import com.bari.discount.service.DiscountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // 목록 조회
    @GetMapping
    public ResponseEntity<?> getDiscounts(
            @RequestParam int page,
            @RequestParam int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(discountService.getAll(pageable));
    }

    // 상세 조회
    @GetMapping("/{discountId}")
    public ResponseEntity<?> getDiscount(@PathVariable Long discountId) {
        return ResponseEntity.ok(discountService.get(discountId));
    }

    // 등록
    @PostMapping
    public ResponseEntity<?> create(@RequestBody DiscountRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(discountService.create(dto));
    }

    // 수정
    @PutMapping("/{discountId}")
    public ResponseEntity<?> update(
            @PathVariable Long discountId,
            @RequestBody DiscountRequest dto) {

        return ResponseEntity.ok(discountService.update(discountId, dto));
    }

    // 삭제 (종료 + soft delete)
    @DeleteMapping("/{discountId}")
    public ResponseEntity<?> delete(@PathVariable Long discountId) {
        discountService.delete(discountId);
        return ResponseEntity.ok().build();
    }
}
