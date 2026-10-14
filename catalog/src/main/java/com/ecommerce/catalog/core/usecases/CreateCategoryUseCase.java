package com.ecommerce.catalog.core.usecases;

import com.ecommerce.catalog.core.domain.Category;
import com.ecommerce.catalog.core.repositories.CategoryRepository;
import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.events.CategoryCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service: create a category (name must be unique).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public String execute(String name) {
        categoryRepository.findByName(name.trim())
                .ifPresent(c -> {
                    throw new DomainValidationException("category '" + name + "' already exists");
                });

        Category category = Category.create(name);
        categoryRepository.save(category);
        log.info("Catalog: category created [categoryId={}, name={}]", category.getId(), category.getName());

        eventPublisher.publish(CategoryCreated.of(category.getId(), category.getName()));
        return category.getId();
    }
}
