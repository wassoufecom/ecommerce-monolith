package com.ecommerce.payment.presenters.handlers;

import com.ecommerce.payment.core.usecases.ProcessPaymentUseCase;
import com.ecommerce.shared.domain.events.StockCheckedAndReserved;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Saga participant of the payment context: charge the customer after stock is reserved.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSagaHandler {

    private final ProcessPaymentUseCase processPaymentUseCase;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(StockCheckedAndReserved event) {
        log.info("Saga step 3: processing payment for order [orderId={}, amount={}]",
                event.orderId(), event.amount());
        processPaymentUseCase.execute(event.orderId(), event.amount(), event.paymentDetails());
    }
}
