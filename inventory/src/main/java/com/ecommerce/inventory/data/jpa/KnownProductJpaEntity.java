package com.ecommerce.inventory.data.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Local mirror of product ids the inventory context learned about through events.
 * Lets AddStock enforce "product exists" without querying the catalog database.
 */
@Entity
@Table(name = "known_products")
@Getter
@Setter
@NoArgsConstructor
public class KnownProductJpaEntity {

    @Id
    @Column(name = "product_id", length = 36)
    private String productId;

    @Column(name = "title")
    private String title;
}
