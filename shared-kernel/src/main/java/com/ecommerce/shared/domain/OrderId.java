package com.ecommerce.shared.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Shared-kernel value object identifying an order across all domains.
 */
public record OrderId(UUID value) {

    public OrderId {
        Objects.requireNonNull(value, "orderId must not be null");
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    public static OrderId of(String raw) {
        return new OrderId(UUID.fromString(raw));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
