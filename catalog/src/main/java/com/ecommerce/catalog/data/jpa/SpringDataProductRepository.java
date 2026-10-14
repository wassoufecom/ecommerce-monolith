package com.ecommerce.catalog.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, String> {
}
