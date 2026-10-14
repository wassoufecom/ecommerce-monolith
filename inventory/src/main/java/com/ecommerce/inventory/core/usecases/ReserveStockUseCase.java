package com.ecommerce.inventory.core.usecases;

import com.ecommerce.inventory.core.domain.Reservation;
import com.ecommerce.inventory.core.domain.Stock;
import com.ecommerce.inventory.core.repositories.ReservationRepository;
import com.ecommerce.inventory.core.repositories.StockRepository;
import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.PaymentDetails;
import com.ecommerce.shared.domain.events.StockCheckFailed;
import com.ecommerce.shared.domain.events.StockCheckedAndReserved;
import com.ecommerce.shared.domain.events.StockReserved;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReserveStock: triggered by OrderCreated.
 * All-or-nothing: if any item lacks stock, the whole reservation fails and
 * StockCheckFailed is emitted instead. Idempotent per order (one reservation per order).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReserveStockUseCase {

    private final StockRepository stockRepository;
    private final ReservationRepository reservationRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void execute(String orderId, List<OrderLine> items,
                        BigDecimal amount, PaymentDetails paymentDetails) {
        // Idempotency: never double-reserve for the same order.
        if (reservationRepository.findByOrderId(orderId).isPresent()) {
            log.warn("Inventory: reservation for order {} already exists — skipping (idempotent)", orderId);
            return;
        }

        try {
            // Phase 1: validate all items (all-or-nothing).
            Map<String, Integer> requested = new HashMap<>();
            for (OrderLine line : items) {
                Stock stock = stockRepository.findByProductId(line.productId())
                        .orElseThrow(() -> new DomainValidationException(
                                "unknown product " + line.productId()));
                if (stock.getAvailableQty() < line.quantity()) {
                    throw new DomainValidationException(
                            "insufficient stock for product " + line.productId()
                                    + " (available=" + stock.getAvailableQty()
                                    + ", requested=" + line.quantity() + ")");
                }
                requested.merge(line.productId(), line.quantity(), Integer::sum);
            }

            // Phase 2: deduct stock.
            requested.forEach((productId, qty) -> {
                Stock stock = stockRepository.findByProductId(productId).orElseThrow();
                stock.deduct(qty);
                stockRepository.save(stock);
            });

            // Phase 3: persist the reservation.
            reservationRepository.save(Reservation.create(orderId, items));
            log.info("Inventory: stock reserved for order [orderId={}, items={}]", orderId, items);

            // Phase 4: notify.
            eventPublisher.publish(StockReserved.of(orderId, items));
            eventPublisher.publish(StockCheckedAndReserved.of(orderId, amount, paymentDetails));

        } catch (DomainValidationException ex) {
            log.warn("Inventory: stock check failed for order [orderId={}, reason={}]",
                    orderId, ex.getMessage());
            eventPublisher.publish(StockCheckFailed.of(orderId, ex.getMessage()));
        }
    }
}
