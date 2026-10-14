package com.ecommerce.payment.core.usecases;

import com.ecommerce.payment.core.domain.Payment;
import com.ecommerce.payment.core.ports.PaymentGateway;
import com.ecommerce.payment.core.repositories.PaymentRepository;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.PaymentDetails;
import com.ecommerce.shared.domain.events.PaymentCreated;
import com.ecommerce.shared.domain.events.PaymentFailed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * ProcessPayment: triggered by StockCheckedAndReserved.
 * Registers the payment as PENDING, attempts the charge, then emits PaymentCreated / PaymentFailed.
 * Idempotent per order (exactly one payment per order).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway paymentGateway;
    private final EventPublisher eventPublisher;

    @Transactional
    public void execute(String orderId, BigDecimal amount, PaymentDetails paymentDetails) {
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            log.warn("Payment: payment for order {} already exists — skipping (idempotent)", orderId);
            return;
        }

        Payment payment = Payment.pending(orderId, amount, paymentDetails.last4());
        PaymentGateway.Result result = paymentGateway.charge(amount, paymentDetails);

        if (result.success()) {
            payment.complete(result.tokenOrReason());
            paymentRepository.save(payment);
            log.info("Payment: completed [orderId={}, paymentId={}, amount={}]",
                    orderId, payment.getId(), amount);
            eventPublisher.publish(PaymentCreated.of(orderId, payment.getId(), amount, payment.getCardLast4()));
        } else {
            payment.fail(result.tokenOrReason());
            paymentRepository.save(payment);
            log.warn("Payment: failed [orderId={}, paymentId={}, reason={}]",
                    orderId, payment.getId(), result.tokenOrReason());
            eventPublisher.publish(PaymentFailed.of(orderId, payment.getId(), result.tokenOrReason(), amount, payment.getCardLast4()));
        }
    }
}
