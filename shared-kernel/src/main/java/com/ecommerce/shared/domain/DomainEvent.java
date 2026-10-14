package com.ecommerce.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Contract for transient, in-process domain events.
 * Events are immutable facts, named in the past tense, and are NEVER persisted
 * (no event store in the monolith — state is stored in JPA tables).
 */
public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();
}
