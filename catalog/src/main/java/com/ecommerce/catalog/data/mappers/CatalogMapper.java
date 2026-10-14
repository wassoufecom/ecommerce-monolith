package com.ecommerce.catalog.data.mappers;

import com.ecommerce.catalog.core.domain.Category;
import com.ecommerce.catalog.core.domain.Product;
import com.ecommerce.catalog.data.jpa.CategoryJpaEntity;
import com.ecommerce.catalog.data.jpa.ProductJpaEntity;

import java.util.List;

public final class CatalogMapper {

    private CatalogMapper() {
    }

    public static ProductJpaEntity toJpa(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.setId(product.getId());
        entity.setTitle(product.getTitle());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        return entity;
    }

    public static Product toDomain(ProductJpaEntity entity, List<String> categoryIds) {
        return Product.reconstitute(entity.getId(), entity.getTitle(), entity.getDescription(),
                entity.getPrice(), categoryIds);
    }

    public static CategoryJpaEntity toJpa(Category category) {
        CategoryJpaEntity entity = new CategoryJpaEntity();
        entity.setId(category.getId());
        entity.setName(category.getName());
        return entity;
    }

    public static Category toDomain(CategoryJpaEntity entity) {
        return Category.reconstitute(entity.getId(), entity.getName());
    }
}
