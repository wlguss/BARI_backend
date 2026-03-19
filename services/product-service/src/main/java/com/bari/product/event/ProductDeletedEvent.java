package com.bari.product.event;

public record ProductDeletedEvent(
    Long productId,
    Long storeId
) {
}
