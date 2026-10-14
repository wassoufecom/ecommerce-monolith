package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;
import com.ecommerce.shared.domain.OrderLine;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Inventory-internal fact: a reservation was released (payment failed compensation). */
public record StockReleased(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        List<OrderLine> items
) implements DomainEvent {

    public static StockReleased of(String orderId, List<OrderLine> items) {
        return new StockReleased(UUID.randomUUID(), Instant.now(), orderId, List.copyOf(items));
    }
}
