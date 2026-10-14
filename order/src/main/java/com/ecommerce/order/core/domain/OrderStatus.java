package com.ecommerce.order.core.domain;

/**
 * Order lifecycle driven entirely by saga events.
 * PENDING → STOCK_RESERVED → PAYMENT_COMPLETED → CONFIRMED
 * Failure paths: REJECTED (stock), PAYMENT_FAILED (payment), DELIVERY_FAILED (delivery, future).
 */
public enum OrderStatus {
    PENDING,
    STOCK_RESERVED,
    PAYMENT_COMPLETED,
    CONFIRMED,
    REJECTED,
    PAYMENT_FAILED,
    DELIVERY_FAILED
}
