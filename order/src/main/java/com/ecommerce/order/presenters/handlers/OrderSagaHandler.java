package com.ecommerce.order.presenters.handlers;

import com.ecommerce.order.core.usecases.UpdateOrderStatusUseCase;
import com.ecommerce.shared.domain.events.DeliveryScheduled;
import com.ecommerce.shared.domain.events.PaymentCreated;
import com.ecommerce.shared.domain.events.PaymentFailed;
import com.ecommerce.shared.domain.events.StockCheckFailed;
import com.ecommerce.shared.domain.events.StockCheckedAndReserved;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Saga participants of the order context: react to other domains' events AFTER their commit.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaHandler {

    private final UpdateOrderStatusUseCase updateOrderStatusUseCase;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(StockCheckedAndReserved event) {
        log.info("Saga: order [{}] stock reserved — awaiting payment", event.orderId());
        updateOrderStatusUseCase.markStockReserved(event.orderId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(StockCheckFailed event) {
        log.warn("Saga: order [{}] rejected — {}", event.orderId(), event.reason());
        updateOrderStatusUseCase.reject(event.orderId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentCreated event) {
        log.info("Saga: order [{}] payment [{}] completed — awaiting delivery", event.orderId(), event.paymentId());
        updateOrderStatusUseCase.markPaymentCompleted(event.orderId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentFailed event) {
        log.warn("Saga: order [{}] payment failed — {}", event.orderId(), event.reason());
        updateOrderStatusUseCase.markPaymentFailed(event.orderId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(DeliveryScheduled event) {
        log.info("Saga complete: order [{}] confirmed, delivery scheduled to {}", event.orderId(), event.address().city());
        updateOrderStatusUseCase.markConfirmed(event.orderId());
    }
}
