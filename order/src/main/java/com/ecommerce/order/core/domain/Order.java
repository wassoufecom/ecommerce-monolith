package com.ecommerce.order.core.domain;

import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.ShippingAddress;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Order aggregate root (state-stored).
 * Invariants: >= 1 item, quantity >= 1 per item, positive total matching the item sum,
 * complete shipping address, userId present. Items and address are immutable once created.
 */
@Getter
public class Order {

    private String id;
    private String userId;
    private List<OrderLine> items;
    private BigDecimal totalAmount;
    private ShippingAddress address;
    private OrderStatus status;

    private Order() {
    }

    private Order(String id, String userId, List<OrderLine> items, BigDecimal totalAmount,
                  ShippingAddress address, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.items = List.copyOf(items);
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
    }

    public static Order create(String userId, List<OrderLine> items, BigDecimal claimedTotal,
                               ShippingAddress address) {
        if (userId == null || userId.isBlank()) {
            throw new DomainValidationException("order must have a userId");
        }
        if (items == null || items.isEmpty()) {
            throw new DomainValidationException("order must contain at least one item");
        }
        if (claimedTotal == null || claimedTotal.signum() <= 0) {
            throw new DomainValidationException("order total price must be positive");
        }
        // OrderLine / ShippingAddress constructors already enforce per-item and address rules.
        BigDecimal computed = items.stream()
                .map(OrderLine::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (computed.compareTo(claimedTotal) != 0) {
            throw new DomainValidationException(
                    "order total " + claimedTotal + " does not match item sum " + computed);
        }
        return new Order(java.util.UUID.randomUUID().toString(), userId, items, computed,
                address, OrderStatus.PENDING);
    }

    /** Rebuilds the aggregate from persisted current state (state-stored — no event replay). */
    public static Order reconstitute(String id, String userId, List<OrderLine> items,
                                     BigDecimal totalAmount, ShippingAddress address, OrderStatus status) {
        return new Order(id, userId, items, totalAmount, address, status);
    }

    public void markStockReserved() {
        this.status = OrderStatus.STOCK_RESERVED;
    }

    public void markPaymentCompleted() {
        this.status = OrderStatus.PAYMENT_COMPLETED;
    }

    public void markConfirmed() {
        this.status = OrderStatus.CONFIRMED;
    }

    public void reject() {
        this.status = OrderStatus.REJECTED;
    }

    public void markPaymentFailed() {
        this.status = OrderStatus.PAYMENT_FAILED;
    }

    public void markDeliveryFailed() {
        this.status = OrderStatus.DELIVERY_FAILED;
    }
}
