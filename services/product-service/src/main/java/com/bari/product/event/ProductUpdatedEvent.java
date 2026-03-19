package com.bari.product.event;

public record ProductUpdatedEvent(
    Long productId,
    Long storeId
) {
}
