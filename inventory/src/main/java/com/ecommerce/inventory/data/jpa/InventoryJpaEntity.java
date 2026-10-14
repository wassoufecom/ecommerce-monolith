package com.ecommerce.inventory.data.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class InventoryJpaEntity {

    @Id
    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "available_qty", nullable = false)
    private int availableQty;
}
