package com.ecommerce.catalog.core.domain;

import com.ecommerce.shared.domain.DomainValidationException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Product aggregate root. Invariant: non-empty title, positive price, zero or more categories.
 */
@Getter
public class Product {

    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private List<String> categoryIds;

    private Product() {
    }

    private Product(String id, String title, String description, BigDecimal price, List<String> categoryIds) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.categoryIds = List.copyOf(categoryIds);
    }

    public static Product create(String title, String description, BigDecimal price, List<String> categoryIds) {
        if (title == null || title.isBlank()) {
            throw new DomainValidationException("product title must not be blank");
        }
        if (price == null || price.signum() <= 0) {
            throw new DomainValidationException("product price must be positive");
        }
        return new Product(UUID.randomUUID().toString(), title, description, price,
                categoryIds == null ? List.of() : categoryIds);
    }

    /** Rebuilds the aggregate from persisted current state (state-stored — no event replay). */
    public static Product reconstitute(String id, String title, String description,
                                       BigDecimal price, List<String> categoryIds) {
        return new Product(id, title, description, price,
                categoryIds == null ? List.of() : categoryIds);
    }
}
