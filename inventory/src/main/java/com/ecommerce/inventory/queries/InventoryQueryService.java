package com.ecommerce.inventory.queries;

import com.ecommerce.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Query side of the inventory context — reads from inventory_read_model only.
 */
@Service
@RequiredArgsConstructor
public class InventoryQueryService {

    public record StockView(String productId, int availableQty, int reservedQty) implements java.io.Serializable {
    }

    private final SpringDataInventoryReadRepository readRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "inventory", key = "'all'")
    public List<StockView> listStock() {
        return readRepository.findAllByOrderByProductIdAsc().stream()
                .map(m -> new StockView(m.getProductId(), m.getAvailableQty(), m.getReservedQty()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "inventory", key = "#productId")
    public StockView getStock(String productId) {
        return readRepository.findById(productId)
                .map(m -> new StockView(m.getProductId(), m.getAvailableQty(), m.getReservedQty()))
                .orElseThrow(() -> new NotFoundException("no stock entry for product " + productId));
    }
}
