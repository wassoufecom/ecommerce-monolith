package com.ecommerce.catalog.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductCategoryRepository extends JpaRepository<ProductCategoryJpaEntity, ProductCategoryJpaEntity.Pk> {

    List<ProductCategoryJpaEntity> findByProductId(String productId);

    void deleteByProductId(String productId);
}
