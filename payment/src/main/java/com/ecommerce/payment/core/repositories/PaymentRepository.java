package com.ecommerce.payment.core.repositories;

import com.ecommerce.payment.core.domain.Payment;

import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findByOrderId(String orderId);
}
