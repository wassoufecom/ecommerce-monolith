package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;
import com.ecommerce.shared.domain.PaymentDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Step 2 success — emitted by the Inventory domain once stock was reserved.
 * Consumer: Payment domain (create + charge the payment).
 * Carries the amount and payment details so Payment never has to call back into other domains.
 */
public record StockCheckedAndReserved(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        BigDecimal amount,
        PaymentDetails paymentDetails
) implements DomainEvent {

    public static StockCheckedAndReserved of(String orderId, BigDecimal amount,
                                             PaymentDetails paymentDetails) {
        return new StockCheckedAndReserved(UUID.randomUUID(), Instant.now(),
                orderId, amount, paymentDetails);
    }
}
