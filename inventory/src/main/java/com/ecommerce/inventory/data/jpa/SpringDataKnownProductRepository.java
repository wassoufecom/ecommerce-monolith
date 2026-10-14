package com.ecommerce.inventory.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataKnownProductRepository extends JpaRepository<KnownProductJpaEntity, String> {
}
