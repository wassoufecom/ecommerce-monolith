package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.PaymentDetails;
import com.ecommerce.shared.domain.ShippingAddress;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Step 1 of the saga — emitted by the Order domain after an order is created.
 * Carries everything downstream domains need (no back-calls allowed).
 * Consumers: Inventory (reserve stock), Delivery (address snapshot), Order (projection).
 */
public record OrderCreated(
        UUID eventId,
        Instant occurredAt,
        String orderId,
        String userId,
        List<OrderLine> items,
        BigDecimal totalAmount,
        ShippingAddress address,
        PaymentDetails paymentDetails
) implements DomainEvent {

    public static OrderCreated of(String orderId, String userId, List<OrderLine> items,
                                  BigDecimal totalAmount, ShippingAddress address,
                                  PaymentDetails paymentDetails) {
        return new OrderCreated(UUID.randomUUID(), Instant.now(), orderId, userId,
                List.copyOf(items), totalAmount, address, paymentDetails);
    }
}
