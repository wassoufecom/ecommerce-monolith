package com.ecommerce.delivery.data.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_address_snapshots")
@Getter
@Setter
@NoArgsConstructor
public class OrderAddressSnapshotJpaEntity {

    @Id
    @Column(name = "order_id", length = 36)
    private String orderId;

    @Column(name = "user_id", length = 64)
    private String userId;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "zip", nullable = false)
    private String zip;

    @Column(name = "country", nullable = false)
    private String country;
}
