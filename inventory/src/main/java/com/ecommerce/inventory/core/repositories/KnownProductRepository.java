package com.ecommerce.inventory.core.repositories;

public interface KnownProductRepository {

    boolean exists(String productId);

    void save(String productId, String title);
}
