package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/** Inventory-internal fact: stock was added for a product. */
public record StockAdded(
        UUID eventId,
        Instant occurredAt,
        String productId,
        int quantity
) implements DomainEvent {

    public static StockAdded of(String productId, int quantity) {
        return new StockAdded(UUID.randomUUID(), Instant.now(), productId, quantity);
    }
}
