package com.ecommerce.delivery.queries;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "delivery_read_model")
@Getter
@Setter
@NoArgsConstructor
public class DeliveryReadModel {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "order_id", length = 36, nullable = false, unique = true)
    private String orderId;

    @Column(name = "status", nullable = false, length = 16)
    private String status;

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
