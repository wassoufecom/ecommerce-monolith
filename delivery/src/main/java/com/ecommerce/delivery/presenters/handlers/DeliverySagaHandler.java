package com.ecommerce.delivery.presenters.handlers;

import com.ecommerce.delivery.core.usecases.RememberOrderAddressUseCase;
import com.ecommerce.delivery.core.usecases.ScheduleDeliveryUseCase;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.events.DeliveryScheduled;
import com.ecommerce.shared.domain.events.OrderCreated;
import com.ecommerce.shared.domain.events.PaymentCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Saga participants of the delivery context.
 *
 * The address snapshot is written IN the order transaction (@EventListener): it is a
 * delivery-owned projection of OrderCreated, and writing it in-tx guarantees it is
 * committed before ANY after-commit saga step (e.g. PaymentCreated -> schedule) can run.
 *
 * Scheduling runs in its own transaction; DeliveryScheduled is published only after that
 * transaction commits, so downstream query projections read committed state.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliverySagaHandler {

    private final RememberOrderAddressUseCase rememberOrderAddressUseCase;
    private final ScheduleDeliveryUseCase scheduleDeliveryUseCase;
    private final EventPublisher eventPublisher;

    @EventListener
    public void on(OrderCreated event) {
        rememberOrderAddressUseCase.execute(event.orderId(), event.userId(), event.address());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaymentCreated event) {
        log.info("Saga step 4: scheduling delivery for order [orderId={}]", event.orderId());
        var delivery = scheduleDeliveryUseCase.execute(event.orderId());
        if (delivery != null) {
            eventPublisher.publish(DeliveryScheduled.of(event.orderId(), delivery.getAddress()));
        }
    }
}
