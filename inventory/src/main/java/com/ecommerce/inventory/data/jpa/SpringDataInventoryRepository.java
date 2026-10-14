package com.ecommerce.inventory.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataInventoryRepository extends JpaRepository<InventoryJpaEntity, String> {
}
