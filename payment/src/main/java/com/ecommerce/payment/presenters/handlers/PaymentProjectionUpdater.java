package com.ecommerce.payment.presenters.handlers;

import com.ecommerce.payment.queries.PaymentReadModel;
import com.ecommerce.payment.queries.SpringDataPaymentReadRepository;
import com.ecommerce.shared.domain.events.PaymentCreated;
import com.ecommerce.shared.domain.events.PaymentFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Projection updater: keeps payment_read_model in sync with transient domain events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentProjectionUpdater {

    private final SpringDataPaymentReadRepository readRepository;

    @EventListener
    public void on(PaymentCreated event) {
        PaymentReadModel readModel = findOrCreate(event.orderId());
        readModel.setId(event.paymentId());
        readModel.setAmount(event.amount());
        readModel.setStatus("COMPLETED");
        readModel.setCardLast4(event.cardLast4());
        readModel.setFailureReason(null);
        readRepository.save(readModel);
        log.info("Payment projection: payment_read_model updated [orderId={}, status=COMPLETED]",
                event.orderId());
    }

    @EventListener
    public void on(PaymentFailed event) {
        PaymentReadModel readModel = findOrCreate(event.orderId());
        readModel.setId(event.paymentId());
        readModel.setAmount(event.amount());
        readModel.setStatus("FAILED");
        readModel.setCardLast4(event.cardLast4());
        readModel.setFailureReason(event.reason());
        readRepository.save(readModel);
        log.info("Payment projection: payment_read_model updated [orderId={}, status=FAILED]",
                event.orderId());
    }

    private PaymentReadModel findOrCreate(String orderId) {
        return readRepository.findByOrderId(orderId).orElseGet(() -> {
            PaymentReadModel readModel = new PaymentReadModel();
            readModel.setOrderId(orderId);
            readModel.setStatus("PENDING");
            readModel.setCreatedAt(Instant.now());
            return readModel;
        });
    }
}
