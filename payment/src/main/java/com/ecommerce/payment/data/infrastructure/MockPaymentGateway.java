package com.ecommerce.payment.data.infrastructure;

import com.ecommerce.payment.core.ports.PaymentGateway;
import com.ecommerce.shared.domain.PaymentDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mock payment gateway (real PSP integration is out of scope).
 * Test card 4000000000000002 is always declined — used to exercise the failure path.
 */
@Slf4j
@Component
public class MockPaymentGateway implements PaymentGateway {

    public static final String DECLINE_TEST_CARD = "4000000000000002";

    @Override
    public Result charge(BigDecimal amount, PaymentDetails paymentDetails) {
        if (paymentDetails.cardNumber().equals(DECLINE_TEST_CARD)) {
            log.warn("Mock gateway: card declined [last4={}]", paymentDetails.last4());
            return new Result(false, "card declined by issuer (mock)");
        }
        String token = "tok_" + UUID.randomUUID();
        log.info("Mock gateway: charge approved [amount={}, last4={}, token={}]",
                amount, paymentDetails.last4(), token);
        return new Result(true, token);
    }
}
