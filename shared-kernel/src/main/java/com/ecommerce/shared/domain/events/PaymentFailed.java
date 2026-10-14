package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Step 3 failure — emitted by the Payment domain when the charge failed.
 * Consumers: Order domain (mark PAYMENT_FAILED), Inventory domain (release reserved stock).
 */
public record PaymentFailed(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        String paymentId,
        String reason,
        BigDecimal amount,
        String cardLast4
) implements DomainEvent {

    public static PaymentFailed of(String orderId, String paymentId, String reason,
                                   BigDecimal amount, String cardLast4) {
        return new PaymentFailed(UUID.randomUUID(), Instant.now(), orderId, paymentId, reason,
                amount, cardLast4);
    }
}
