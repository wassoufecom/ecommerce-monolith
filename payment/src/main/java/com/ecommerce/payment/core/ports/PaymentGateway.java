package com.ecommerce.payment.core.ports;

import com.ecommerce.shared.domain.PaymentDetails;

import java.math.BigDecimal;

/**
 * Outbound port for charging a card. Swapping the mock for a real PSP is a one-class change.
 */
public interface PaymentGateway {

    Result charge(BigDecimal amount, PaymentDetails paymentDetails);

    record Result(boolean success, String tokenOrReason) {
    }
}
