package com.ecommerce.shared.domain;

import java.math.BigDecimal;

/**
 * Value object describing a line inside an order (product + quantity + unit price).
 */
public record OrderLine(String productId, int quantity, BigDecimal unitPrice) {

    public OrderLine {
        if (productId == null || productId.isBlank()) {
            throw new DomainValidationException("order line productId must not be blank");
        }
        if (quantity < 1) {
            throw new DomainValidationException("order line quantity must be >= 1");
        }
        if (unitPrice == null || unitPrice.signum() <= 0) {
            throw new DomainValidationException("order line unitPrice must be positive");
        }
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
