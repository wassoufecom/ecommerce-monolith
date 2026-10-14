package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * Step 2 failure — emitted by the Inventory domain when stock could not be reserved.
 * Consumer: Order domain (mark order REJECTED).
 */
public record StockCheckFailed(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        String reason
) implements DomainEvent {

    public static StockCheckFailed of(String orderId, String reason) {
        return new StockCheckFailed(UUID.randomUUID(), Instant.now(), orderId, reason);
    }
}
