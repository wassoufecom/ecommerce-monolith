package com.ecommerce.catalog.data.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "product_categories")
@IdClass(ProductCategoryJpaEntity.Pk.class)
@Getter
@Setter
@NoArgsConstructor
public class ProductCategoryJpaEntity {

    @Id
    @Column(name = "product_id", length = 36)
    private String productId;

    @Id
    @Column(name = "category_id", length = 36)
    private String categoryId;

    public static class Pk implements Serializable {
        private String productId;
        private String categoryId;

        public Pk() {
        }

        public Pk(String productId, String categoryId) {
            this.productId = productId;
            this.categoryId = categoryId;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Pk pk && productId.equals(pk.productId) && categoryId.equals(pk.categoryId);
        }

        @Override
        public int hashCode() {
            return (productId + ":" + categoryId).hashCode();
        }
    }
}
