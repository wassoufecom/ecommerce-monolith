package com.ecommerce.catalog.data.implementations;

import com.ecommerce.catalog.core.domain.Product;
import com.ecommerce.catalog.core.repositories.ProductRepository;
import com.ecommerce.catalog.data.jpa.ProductCategoryJpaEntity;
import com.ecommerce.catalog.data.jpa.SpringDataProductCategoryRepository;
import com.ecommerce.catalog.data.jpa.SpringDataProductRepository;
import com.ecommerce.catalog.data.mappers.CatalogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final SpringDataProductRepository springData;
    private final SpringDataProductCategoryRepository linkSpringData;

    @Override
    public Product save(Product product) {
        // Parent first — product_categories has an FK to products.
        springData.save(CatalogMapper.toJpa(product));
        linkSpringData.deleteByProductId(product.getId());
        linkSpringData.flush();
        for (String categoryId : product.getCategoryIds()) {
            ProductCategoryJpaEntity link = new ProductCategoryJpaEntity();
            link.setProductId(product.getId());
            link.setCategoryId(categoryId);
            linkSpringData.save(link);
        }
        return CatalogMapper.toDomain(springData.findById(product.getId()).orElseThrow(),
                product.getCategoryIds());
    }

    @Override
    public Optional<Product> findById(String id) {
        return springData.findById(id).map(entity -> {
            List<String> categoryIds = linkSpringData.findByProductId(id).stream()
                    .map(ProductCategoryJpaEntity::getCategoryId)
                    .toList();
            return CatalogMapper.toDomain(entity, categoryIds);
        });
    }
}
