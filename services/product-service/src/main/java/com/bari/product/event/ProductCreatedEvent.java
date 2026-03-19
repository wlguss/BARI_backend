package com.bari.product.event;

public record ProductCreatedEvent(
    
    Long productId,
    Long storeId,
    String name
) {
}
