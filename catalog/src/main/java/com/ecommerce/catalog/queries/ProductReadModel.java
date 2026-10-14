package com.ecommerce.catalog.queries;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Read model for products (query side — updated by @EventListener projections).
 */
@Entity
@Table(name = "product_read_model")
@Getter
@Setter
@NoArgsConstructor
public class ProductReadModel {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /** Comma-separated category names (denormalized for fast reads). */
    @Column(name = "categories")
    private String categories;

    @Column(name = "created_at")
    private Instant createdAt;
}
