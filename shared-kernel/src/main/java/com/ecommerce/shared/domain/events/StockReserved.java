package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;
import com.ecommerce.shared.domain.OrderLine;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Inventory-internal fact: stock was reserved for an order. */
public record StockReserved(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        List<OrderLine> items
) implements DomainEvent {

    public static StockReserved of(String orderId, List<OrderLine> items) {
        return new StockReserved(UUID.randomUUID(), Instant.now(), orderId, List.copyOf(items));
    }
}
