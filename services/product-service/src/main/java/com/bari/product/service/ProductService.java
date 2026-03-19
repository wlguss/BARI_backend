package com.bari.product.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bari.product.dto.request.ProductRequestDTO;
import com.bari.product.dto.request.ProductUpdateDTO;
import com.bari.product.dto.response.ProductResponseDTO;
import com.bari.product.entity.ProductEntity;
import com.bari.product.event.ProductCreatedEvent;
import com.bari.product.event.ProductDeletedEvent;
import com.bari.product.event.ProductUpdatedEvent;
import com.bari.product.exception.ProductErrorCode;
import com.bari.product.exception.ProductException;
import com.bari.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO request, Long userId, String role) {
        validateManager(userId, role);

        ProductEntity product = ProductEntity.create(request);
        ProductEntity saved = productRepository.save(product);

        eventPublisher.publishEvent(new ProductCreatedEvent(
                saved.getId(),
                saved.getStoreId(),
                saved.getName()));

        return ProductResponseDTO.from(saved);
    }

    public ProductResponseDTO getById(Long productId) {
        ProductEntity product = getActiveProduct(productId);
        return ProductResponseDTO.from(product);
    }

    public List<ProductResponseDTO> getAll(Long storeId, String keyword) {
        List<ProductEntity> products;

        if (storeId != null && hasText(keyword)) {
            products = productRepository
                    .findAllByStoreIdAndNameContainingIgnoreCaseAndDeletedAtIsNullOrderByIdDesc(storeId, keyword);
        } else if (storeId != null) {
            products = productRepository.findAllByStoreIdAndDeletedAtIsNullOrderByIdDesc(storeId);
        } else if (hasText(keyword)) {
            products = productRepository.findAllByNameContainingIgnoreCaseAndDeletedAtIsNullOrderByIdDesc(keyword);
        } else {
            products = productRepository.findAllByDeletedAtIsNullOrderByIdDesc();
        }

        return products.stream()
                .map(ProductResponseDTO::from)
                .toList();
    }

    @Transactional
    public ProductResponseDTO update(Long productId, ProductUpdateDTO request, Long userId, String role) {
        validateManager(userId, role);

        ProductEntity product = getActiveProduct(productId);
        product.update(request);

        eventPublisher.publishEvent(new ProductUpdatedEvent(
                product.getId(),
                product.getStoreId()));

        return ProductResponseDTO.from(product);
    }

    @Transactional
    public void delete(Long productId, Long userId, String role) {
        validateManager(userId, role);

        ProductEntity product = getActiveProduct(productId);
        product.softDelete();

        eventPublisher.publishEvent(new ProductDeletedEvent(
                product.getId(),
                product.getStoreId()));
    }

    private ProductEntity getActiveProduct(Long productId) {
        return productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    private void validateManager(Long userId, String role) {
        if (userId == null || userId <= 0) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_ROLE);
        }

        if (!"OWNER".equals(role) && !"ADMIN".equals(role)) {
            throw new ProductException(ProductErrorCode.INVALID_PRODUCT_ROLE);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}