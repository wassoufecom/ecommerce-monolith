package com.ecommerce.payment.queries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPaymentReadRepository extends JpaRepository<PaymentReadModel, String> {

    Optional<PaymentReadModel> findByOrderId(String orderId);
}
