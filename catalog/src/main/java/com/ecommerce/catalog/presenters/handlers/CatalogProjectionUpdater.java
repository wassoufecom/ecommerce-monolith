package com.ecommerce.catalog.presenters.handlers;

import com.ecommerce.catalog.queries.CategoryReadModel;
import com.ecommerce.catalog.queries.ProductReadModel;
import com.ecommerce.catalog.queries.SpringDataCategoryReadRepository;
import com.ecommerce.catalog.queries.SpringDataProductReadRepository;
import com.ecommerce.shared.domain.events.CategoryCreated;
import com.ecommerce.shared.domain.events.ProductCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Projection updater: keeps catalog read-model tables in sync with transient domain events.
 * Runs inside the producing transaction (@EventListener) so write + projection commit atomically.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogProjectionUpdater {

    private final SpringDataProductReadRepository productReadRepository;
    private final SpringDataCategoryReadRepository categoryReadRepository;

    @EventListener
    public void on(ProductCreated event) {
        ProductReadModel readModel = productReadRepository.findById(event.productId())
                .orElseGet(ProductReadModel::new);
        readModel.setId(event.productId());
        readModel.setTitle(event.title());
        readModel.setDescription(event.description());
        readModel.setPrice(event.price());
        readModel.setCategories(event.categoryNames().stream().collect(Collectors.joining(",")));
        if (readModel.getCreatedAt() == null) {
            readModel.setCreatedAt(Instant.now());
        }
        productReadRepository.save(readModel);
        log.info("Catalog projection: product_read_model updated [productId={}]", event.productId());
    }

    @EventListener
    public void on(CategoryCreated event) {
        CategoryReadModel readModel = new CategoryReadModel();
        readModel.setId(event.categoryId());
        readModel.setName(event.name());
        categoryReadRepository.save(readModel);
        log.info("Catalog projection: category_read_model updated [categoryId={}]", event.categoryId());
    }
}
