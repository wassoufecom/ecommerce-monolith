package com.ecommerce.delivery.core.domain;

import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.ShippingAddress;
import lombok.Getter;

import java.util.UUID;

/**
 * Delivery aggregate root (state-stored).
 * Invariants: references exactly one order, carries the order's shipping address,
 * exactly one active delivery per order (enforced by the unique order_id constraint).
 */
@Getter
public class Delivery {

    private String id;
    private String orderId;
    private ShippingAddress address;
    private DeliveryStatus status;

    private Delivery() {
    }

    private Delivery(String id, String orderId, ShippingAddress address, DeliveryStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.address = address;
        this.status = status;
    }

    public static Delivery schedule(String orderId, ShippingAddress address) {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainValidationException("delivery must reference an order");
        }
        if (address == null) {
            throw new DomainValidationException("delivery requires the order's shipping address");
        }
        return new Delivery(UUID.randomUUID().toString(), orderId, address, DeliveryStatus.SCHEDULED);
    }

    public static Delivery reconstitute(String id, String orderId, ShippingAddress address,
                                        DeliveryStatus status) {
        return new Delivery(id, orderId, address, status);
    }
}
