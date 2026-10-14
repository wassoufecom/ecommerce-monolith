package com.ecommerce.inventory.core.domain;

import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.OrderLine;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Reservation aggregate: a temporary hold on stock while an order is processed.
 * Exactly one reservation per order.
 */
@Getter
public class Reservation {

    public enum Status {ACTIVE, RELEASED}

    private String id;
    private String orderId;
    private List<OrderLine> items;
    private Status status;

    private Reservation() {
    }

    private Reservation(String id, String orderId, List<OrderLine> items, Status status) {
        this.id = id;
        this.orderId = orderId;
        this.items = List.copyOf(items);
        this.status = status;
    }

    public static Reservation create(String orderId, List<OrderLine> items) {
        if (items == null || items.isEmpty()) {
            throw new DomainValidationException("reservation must contain at least one item");
        }
        return new Reservation(UUID.randomUUID().toString(), orderId, items, Status.ACTIVE);
    }

    public static Reservation reconstitute(String id, String orderId, List<OrderLine> items, Status status) {
        return new Reservation(id, orderId, items, status);
    }

    public void release() {
        this.status = Status.RELEASED;
    }

    public boolean isActive() {
        return status == Status.ACTIVE;
    }
}
