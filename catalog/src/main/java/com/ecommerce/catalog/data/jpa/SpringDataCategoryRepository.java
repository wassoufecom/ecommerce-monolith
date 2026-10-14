package com.ecommerce.catalog.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataCategoryRepository extends JpaRepository<CategoryJpaEntity, String> {

    Optional<CategoryJpaEntity> findByName(String name);
}
