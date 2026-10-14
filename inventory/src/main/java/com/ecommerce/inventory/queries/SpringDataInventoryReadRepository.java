package com.ecommerce.inventory.queries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataInventoryReadRepository extends JpaRepository<InventoryReadModel, String> {

    List<InventoryReadModel> findAllByOrderByProductIdAsc();
}
