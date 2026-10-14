package com.ecommerce.inventory.presenters.handlers;

import com.ecommerce.inventory.core.usecases.ReleaseStockUseCase;
import com.ecommerce.inventory.core.usecases.ReserveStockUseCase;
import com.ecommerce.shared.domain.events.OrderCreated;
import com.ecommerce.shared.domain.events.PaymentFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Saga participants of the inventory context.
 * AFTER_COMMIT: reacts only to events that were actually committed by their producer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InventorySagaHandler {

    private final ReserveStockUseCase reserveStockUseCase;
    private final ReleaseStockUseCase releaseStockUseCase;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(OrderCreated event) {
        log.info("Saga step 2: reserving stock for order [orderId={}]", event.orderId());
        reserveStockUseCase.execute(event.orderId(), event.items(),
                event.totalAmount(), event.paymentDetails());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentFailed event) {
        log.info("Compensation: releasing stock for failed payment [orderId={}, reason={}]",
                event.orderId(), event.reason());
        releaseStockUseCase.execute(event.orderId());
    }
}
