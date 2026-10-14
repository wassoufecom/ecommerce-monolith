package com.ecommerce.catalog.core.usecases;

import com.ecommerce.catalog.core.domain.Category;
import com.ecommerce.catalog.core.domain.Product;
import com.ecommerce.catalog.core.repositories.CategoryRepository;
import com.ecommerce.catalog.core.repositories.ProductRepository;
import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.events.ProductCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Application service: create a product, resolving categories by name inside the catalog context.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public String execute(String title, String description, BigDecimal price, List<String> categoryNames) {
        List<String> categoryIds = new ArrayList<>();
        if (categoryNames != null) {
            for (String name : categoryNames) {
                Category category = categoryRepository.findByName(name)
                        .orElseThrow(() -> new DomainValidationException(
                                "category '" + name + "' does not exist — create it first"));
                categoryIds.add(category.getId());
            }
        }

        Product product = Product.create(title, description, price, categoryIds);
        productRepository.save(product);
        log.info("Catalog: product created [productId={}, title={}, price={}]", product.getId(), title, price);

        eventPublisher.publish(ProductCreated.of(product.getId(), title, description, price,
                categoryNames == null ? List.of() : categoryNames));
        return product.getId();
    }
}
