package com.ecommerce.catalog.data.implementations;

import com.ecommerce.catalog.core.domain.Category;
import com.ecommerce.catalog.core.repositories.CategoryRepository;
import com.ecommerce.catalog.data.jpa.SpringDataCategoryRepository;
import com.ecommerce.catalog.data.mappers.CatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final SpringDataCategoryRepository springData;

    @Override
    public Category save(Category category) {
        springData.save(CatalogMapper.toJpa(category));
        return category;
    }

    @Override
    public Optional<Category> findById(String id) {
        return springData.findById(id).map(CatalogMapper::toDomain);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return springData.findByName(name).map(CatalogMapper::toDomain);
    }
}
