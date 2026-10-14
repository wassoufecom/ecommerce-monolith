package com.ecommerce.inventory.presenters.handlers;

import com.ecommerce.inventory.queries.InventoryReadModel;
import com.ecommerce.inventory.queries.SpringDataInventoryReadRepository;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.events.StockAdded;
import com.ecommerce.shared.domain.events.StockReleased;
import com.ecommerce.shared.domain.events.StockReserved;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Projection updater: keeps inventory_read_model in sync with transient domain events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryProjectionUpdater {

    private final SpringDataInventoryReadRepository readRepository;

    @EventListener
    public void on(StockAdded event) {
        InventoryReadModel readModel = findOrCreate(event.productId());
        readModel.setAvailableQty(readModel.getAvailableQty() + event.quantity());
        readRepository.save(readModel);
        log.info("Inventory projection: stock added [productId={}, available={}]",
                event.productId(), readModel.getAvailableQty());
    }

    @EventListener
    public void on(StockReserved event) {
        for (Map.Entry<String, Integer> entry : sumByProduct(event.items()).entrySet()) {
            InventoryReadModel readModel = findOrCreate(entry.getKey());
            readModel.setAvailableQty(readModel.getAvailableQty() - entry.getValue());
            readModel.setReservedQty(readModel.getReservedQty() + entry.getValue());
            readRepository.save(readModel);
        }
        log.info("Inventory projection: stock reserved [orderId={}]", event.orderId());
    }

    @EventListener
    public void on(StockReleased event) {
        for (Map.Entry<String, Integer> entry : sumByProduct(event.items()).entrySet()) {
            InventoryReadModel readModel = findOrCreate(entry.getKey());
            readModel.setAvailableQty(readModel.getAvailableQty() + entry.getValue());
            readModel.setReservedQty(readModel.getReservedQty() - entry.getValue());
            readRepository.save(readModel);
        }
        log.info("Inventory projection: stock released [orderId={}]", event.orderId());
    }

    private InventoryReadModel findOrCreate(String productId) {
        return readRepository.findById(productId).orElseGet(() -> {
            InventoryReadModel readModel = new InventoryReadModel();
            readModel.setProductId(productId);
            readModel.setAvailableQty(0);
            readModel.setReservedQty(0);
            return readModel;
        });
    }

    private Map<String, Integer> sumByProduct(java.util.List<OrderLine> items) {
        Map<String, Integer> summed = new HashMap<>();
        items.forEach(line -> summed.merge(line.productId(), line.quantity(), Integer::sum));
        return summed;
    }
}
