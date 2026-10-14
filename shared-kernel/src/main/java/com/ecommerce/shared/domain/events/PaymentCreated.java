package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Step 3 success — emitted by the Payment domain once the charge completed.
 * Consumer: Delivery domain (schedule the delivery).
 */
public record PaymentCreated(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        String paymentId,
        BigDecimal amount,
        String cardLast4
) implements DomainEvent {

    public static PaymentCreated of(String orderId, String paymentId, BigDecimal amount, String cardLast4) {
        return new PaymentCreated(UUID.randomUUID(), Instant.now(), orderId, paymentId, amount, cardLast4);
    }
}
