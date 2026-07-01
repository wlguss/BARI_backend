package com.bari.product.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bari.common.response.ApiResponse;
import com.bari.product.dto.request.ProductRequestDTO;
import com.bari.product.dto.request.ProductUpdateDTO;
import com.bari.product.dto.response.ProductResponseDTO;
import com.bari.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 상품정보 등록
    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponseDTO>> create(
            @Valid @RequestBody ProductRequestDTO request,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {

        return ResponseEntity.status(201).body(ApiResponse.created(productService.create(request, userId, role)));
    }

    // 특정 상품 정보 획득
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getById(@PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(productId)));
    }

    // 전체 상품 정보 획득
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAll(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.success(productService.getAll(storeId, keyword)));
    }

    // 상품 정보 수정
    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> update(
            @PathVariable Long productId,
            @Valid @RequestBody ProductUpdateDTO request,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {

        return ResponseEntity.ok(ApiResponse.success(productService.update(productId, request, userId, role)));
    }

    // 상품 정보 삭제 (soft delete 방식)
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long productId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") String role) {

        productService.delete(productId, userId, role);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
