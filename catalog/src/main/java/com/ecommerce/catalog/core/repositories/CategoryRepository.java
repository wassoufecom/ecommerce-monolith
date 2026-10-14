package com.ecommerce.catalog.core.repositories;

import com.ecommerce.catalog.core.domain.Category;

import java.util.Optional;

public interface CategoryRepository {

    Category save(Category category);

    Optional<Category> findById(String id);

    Optional<Category> findByName(String name);
}
