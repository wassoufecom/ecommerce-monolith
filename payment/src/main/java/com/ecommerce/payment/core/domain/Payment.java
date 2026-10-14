package com.ecommerce.payment.core.domain;

import com.ecommerce.shared.domain.DomainValidationException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment aggregate root (state-stored).
 * Invariants: exactly one order, amount equals the order total (enforced at creation),
 * one of PENDING/COMPLETED/FAILED. Only token/last-4 is persisted — never raw card data.
 */
@Getter
public class Payment {

    private String id;
    private String orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String cardLast4;
    private String token;
    private String failureReason;

    private Payment() {
    }

    private Payment(String id, String orderId, BigDecimal amount, PaymentStatus status,
                    String cardLast4, String token, String failureReason) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.cardLast4 = cardLast4;
        this.token = token;
        this.failureReason = failureReason;
    }

    public static Payment pending(String orderId, BigDecimal amount, String cardLast4) {
        if (orderId == null || orderId.isBlank()) {
            throw new DomainValidationException("payment must reference exactly one order");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new DomainValidationException("payment amount must be positive");
        }
        return new Payment(UUID.randomUUID().toString(), orderId, amount, PaymentStatus.PENDING,
                cardLast4, null, null);
    }

    public static Payment reconstitute(String id, String orderId, BigDecimal amount,
                                       PaymentStatus status, String cardLast4, String token,
                                       String failureReason) {
        return new Payment(id, orderId, amount, status, cardLast4, token, failureReason);
    }

    public void complete(String token) {
        this.status = PaymentStatus.COMPLETED;
        this.token = token;
    }

    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }
}
