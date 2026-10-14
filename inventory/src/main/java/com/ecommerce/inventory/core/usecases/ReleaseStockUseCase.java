package com.ecommerce.inventory.core.usecases;

import com.ecommerce.inventory.core.domain.Reservation;
import com.ecommerce.inventory.core.domain.Stock;
import com.ecommerce.inventory.core.repositories.ReservationRepository;
import com.ecommerce.inventory.core.repositories.StockRepository;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.events.StockReleased;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * ReleaseStock: compensation triggered by PaymentFailed — restores reserved stock.
 * Idempotent: releasing an already-released reservation is a no-op.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReleaseStockUseCase {

    private final StockRepository stockRepository;
    private final ReservationRepository reservationRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public void execute(String orderId) {
        Reservation reservation = reservationRepository.findByOrderId(orderId).orElse(null);
        if (reservation == null) {
            log.warn("Inventory: no reservation found for order {} — nothing to release", orderId);
            return;
        }
        if (!reservation.isActive()) {
            log.info("Inventory: reservation for order {} already released — skipping (idempotent)", orderId);
            return;
        }

        Map<String, Integer> restored = new HashMap<>();
        for (OrderLine line : reservation.getItems()) {
            restored.merge(line.productId(), line.quantity(), Integer::sum);
        }
        restored.forEach((productId, qty) -> {
            Stock stock = stockRepository.findByProductId(productId).orElseThrow();
            stock.restore(qty);
            stockRepository.save(stock);
        });

        reservation.release();
        reservationRepository.save(reservation);
        log.info("Inventory: stock released for order [orderId={}, restored={}]", orderId, restored);

        eventPublisher.publish(StockReleased.of(orderId, reservation.getItems()));
    }
}
