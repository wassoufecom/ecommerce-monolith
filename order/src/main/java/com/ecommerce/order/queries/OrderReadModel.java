package com.ecommerce.order.queries;

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
 * Read model for orders (query side — updated by @EventListener projections).
 * Items are stored as a JSON array for cheap single-table reads.
 */
@Entity
@Table(name = "order_read_model")
@Getter
@Setter
@NoArgsConstructor
public class OrderReadModel {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    @Column(name = "status", nullable = false, length = 32)
    private String status;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /** JSON array of {productId, quantity, unitPrice}. */
    @Column(name = "items", columnDefinition = "TEXT")
    private String items;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "zip")
    private String zip;

    @Column(name = "country")
    private String country;

    @Column(name = "created_at")
    private Instant createdAt;
}
