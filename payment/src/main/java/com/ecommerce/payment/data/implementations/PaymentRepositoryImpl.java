package com.ecommerce.payment.data.implementations;

import com.ecommerce.payment.core.domain.Payment;
import com.ecommerce.payment.core.repositories.PaymentRepository;
import com.ecommerce.payment.data.jpa.PaymentJpaEntity;
import com.ecommerce.payment.data.jpa.SpringDataPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final SpringDataPaymentRepository springData;

    @Override
    public Payment save(Payment payment) {
        springData.save(toJpa(payment));
        return payment;
    }

    @Override
    public Optional<Payment> findByOrderId(String orderId) {
        return springData.findByOrderId(orderId).map(PaymentRepositoryImpl::toDomain);
    }

    private static PaymentJpaEntity toJpa(Payment payment) {
        PaymentJpaEntity entity = new PaymentJpaEntity();
        entity.setId(payment.getId());
        entity.setOrderId(payment.getOrderId());
        entity.setAmount(payment.getAmount());
        entity.setStatus(payment.getStatus());
        entity.setCardLast4(payment.getCardLast4());
        entity.setToken(payment.getToken());
        entity.setFailureReason(payment.getFailureReason());
        return entity;
    }

    private static Payment toDomain(PaymentJpaEntity entity) {
        return Payment.reconstitute(entity.getId(), entity.getOrderId(), entity.getAmount(),
                entity.getStatus(), entity.getCardLast4(), entity.getToken(), entity.getFailureReason());
    }
}
