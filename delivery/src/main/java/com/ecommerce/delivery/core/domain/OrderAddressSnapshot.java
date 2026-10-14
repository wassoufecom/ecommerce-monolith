package com.ecommerce.delivery.core.domain;

import com.ecommerce.shared.domain.ShippingAddress;
import lombok.Getter;

/**
 * Local snapshot of an order's shipping address, learned from the OrderCreated event.
 * Lets the delivery context schedule without ever querying the order database.
 */
@Getter
public class OrderAddressSnapshot {

    private String orderId;
    private String userId;
    private ShippingAddress address;

    private OrderAddressSnapshot() {
    }

    private OrderAddressSnapshot(String orderId, String userId, ShippingAddress address) {
        this.orderId = orderId;
        this.userId = userId;
        this.address = address;
    }

    public static OrderAddressSnapshot of(String orderId, String userId, ShippingAddress address) {
        return new OrderAddressSnapshot(orderId, userId, address);
    }

    public static OrderAddressSnapshot reconstitute(String orderId, String userId, ShippingAddress address) {
        return new OrderAddressSnapshot(orderId, userId, address);
    }
}
