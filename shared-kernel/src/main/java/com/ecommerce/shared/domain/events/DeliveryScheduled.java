package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;
import com.ecommerce.shared.domain.ShippingAddress;

import java.time.Instant;
import java.util.UUID;

/**
 * Step 4 — emitted by the Delivery domain once a delivery was scheduled.
 * Consumer: Order domain (mark order CONFIRMED).
 */
public record DeliveryScheduled(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        ShippingAddress address
) implements DomainEvent {

    public static DeliveryScheduled of(String orderId, ShippingAddress address) {
        return new DeliveryScheduled(UUID.randomUUID(), Instant.now(), orderId, address);
    }
}
