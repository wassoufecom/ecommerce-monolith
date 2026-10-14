package com.ecommerce.inventory.core.usecases;

import com.ecommerce.inventory.core.domain.Stock;
import com.ecommerce.inventory.core.repositories.KnownProductRepository;
import com.ecommerce.inventory.core.repositories.StockRepository;
import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.events.StockAdded;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AddStock: increase available stock for a product (quantity > 0, product must exist in catalog).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AddStockUseCase {

    private final StockRepository stockRepository;
    private final KnownProductRepository knownProductRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void execute(String productId, int quantity) {
        if (!knownProductRepository.exists(productId)) {
            throw new DomainValidationException("product " + productId + " does not exist in the catalog");
        }
        Stock stock = stockRepository.findByProductId(productId)
                .orElseGet(() -> Stock.empty(productId));
        stock.add(quantity);
        stockRepository.save(stock);
        log.info("Inventory: stock added [productId={}, quantity={}, available={}]",
                productId, quantity, stock.getAvailableQty());

        eventPublisher.publish(StockAdded.of(productId, quantity));
    }
}
