package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Catalog-internal fact: a product was created. */
public record ProductCreated(
        UUID eventId,
        Instant occurredAt,
        String productId,
        String title,
        String description,
        BigDecimal price,
        List<String> categoryNames
) implements DomainEvent {

    public static ProductCreated of(String productId, String title, String description,
                                    BigDecimal price, List<String> categoryNames) {
        return new ProductCreated(UUID.randomUUID(), Instant.now(), productId, title,
                description, price, List.copyOf(categoryNames));
    }
}
