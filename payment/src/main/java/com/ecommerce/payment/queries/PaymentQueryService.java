package com.ecommerce.payment.queries;

import com.ecommerce.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Query side of the payment context — reads from payment_read_model only.
 */
@Service
@RequiredArgsConstructor
public class PaymentQueryService {

    public record PaymentView(String id, String orderId, BigDecimal amount, String status,
                              String cardLast4, String failureReason, Instant createdAt) implements java.io.Serializable {
    }

    private final SpringDataPaymentReadRepository readRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "payment", key = "#orderId")
    public PaymentView getPaymentByOrderId(String orderId) {
        return readRepository.findByOrderId(orderId)
                .map(m -> new PaymentView(m.getId(), m.getOrderId(), m.getAmount(), m.getStatus(),
                        m.getCardLast4(), m.getFailureReason(), m.getCreatedAt()))
                .orElseThrow(() -> new NotFoundException("no payment for order " + orderId));
    }
}
