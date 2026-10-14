package com.ecommerce.catalog.core.repositories;

import com.ecommerce.catalog.core.domain.Product;

import java.util.Optional;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);
}
