package com.ecommerce.shared.domain.events;

import com.ecommerce.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/** Catalog-internal fact: a category was created. */
public record CategoryCreated(
        UUID eventId,
        Instant occurredAt,
        String categoryId,
        String name
) implements DomainEvent {

    public static CategoryCreated of(String categoryId, String name) {
        return new CategoryCreated(UUID.randomUUID(), Instant.now(), categoryId, name);
    }
}
