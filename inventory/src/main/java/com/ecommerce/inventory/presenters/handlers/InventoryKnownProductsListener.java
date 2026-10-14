package com.ecommerce.inventory.presenters.handlers;

import com.ecommerce.inventory.core.repositories.KnownProductRepository;
import com.ecommerce.shared.domain.events.ProductCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Learns about catalog products through events so the inventory context can
 * enforce "product exists" without ever querying the catalog database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryKnownProductsListener {

    private final KnownProductRepository knownProductRepository;

    @EventListener
    @Transactional
    public void on(ProductCreated event) {
        knownProductRepository.save(event.productId(), event.title());
        log.info("Inventory learned about product [productId={}, title={}]",
                event.productId(), event.title());
    }
}
