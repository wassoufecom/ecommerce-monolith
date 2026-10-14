package com.ecommerce.inventory.core.domain;

import com.ecommerce.shared.domain.DomainValidationException;
import lombok.Getter;

/**
 * Stock aggregate: quantity per product is always >= 0.
 */
@Getter
public class Stock {

    private String productId;
    private int availableQty;

    private Stock() {
    }

    private Stock(String productId, int availableQty) {
        this.productId = productId;
        this.availableQty = availableQty;
    }

    public static Stock of(String productId, int availableQty) {
        return new Stock(productId, availableQty);
    }

    public static Stock empty(String productId) {
        return new Stock(productId, 0);
    }

    public void add(int quantity) {
        if (quantity <= 0) {
            throw new DomainValidationException("stock quantity must be positive");
        }
        this.availableQty += quantity;
    }

    public void deduct(int quantity) {
        if (availableQty < quantity) {
            throw new DomainValidationException(
                    "insufficient stock for product " + productId
                            + " (available=" + availableQty + ", requested=" + quantity + ")");
        }
        this.availableQty -= quantity;
    }

    public void restore(int quantity) {
        this.availableQty += quantity;
    }
}
