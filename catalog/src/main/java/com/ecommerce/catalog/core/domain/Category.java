package com.ecommerce.catalog.core.domain;

import com.ecommerce.shared.domain.DomainValidationException;
import lombok.Getter;

import java.util.UUID;

/**
 * Category aggregate root. Invariant: unique, non-empty name.
 */
@Getter
public class Category {

    private String id;
    private String name;

    private Category() {
    }

    private Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Category create(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainValidationException("category name must not be blank");
        }
        return new Category(UUID.randomUUID().toString(), name.trim());
    }

    /** Rebuilds the aggregate from persisted current state (state-stored — no event replay). */
    public static Category reconstitute(String id, String name) {
        return new Category(id, name);
    }
}
