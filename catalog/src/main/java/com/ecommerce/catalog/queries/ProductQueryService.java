package com.ecommerce.catalog.queries;

import com.ecommerce.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Query side of the catalog context — reads from read-model tables only.
 * Cached in Redis (cache-aside, 60s TTL).
 */
@Service
@RequiredArgsConstructor
public class ProductQueryService {

    public record ProductView(String id, String title, String description, BigDecimal price,
                              List<String> categories) implements java.io.Serializable {
    }

    public record CategoryView(String id, String name) implements java.io.Serializable {
    }

    private final SpringDataProductReadRepository productReadRepository;
    private final SpringDataCategoryReadRepository categoryReadRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "products", key = "'all'")
    public List<ProductView> listProducts() {
        return productReadRepository.findAllByOrderByTitleAsc().stream()
                .map(p -> new ProductView(p.getId(), p.getTitle(), p.getDescription(), p.getPrice(),
                        p.getCategories() == null || p.getCategories().isBlank()
                                ? List.of() : List.of(p.getCategories().split(","))))
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "product", key = "#id")
    public ProductView getProduct(String id) {
        return productReadRepository.findById(id)
                .map(p -> new ProductView(p.getId(), p.getTitle(), p.getDescription(), p.getPrice(),
                        p.getCategories() == null || p.getCategories().isBlank()
                                ? List.of() : List.of(p.getCategories().split(","))))
                .orElseThrow(() -> new NotFoundException("product " + id + " not found"));
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "categories", key = "'all'")
    public List<CategoryView> listCategories() {
        return categoryReadRepository.findAllByOrderByNameAsc().stream()
                .map(c -> new CategoryView(c.getId(), c.getName()))
                .toList();
    }
}
