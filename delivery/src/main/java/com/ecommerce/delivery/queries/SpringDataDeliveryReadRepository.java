package com.ecommerce.delivery.queries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataDeliveryReadRepository extends JpaRepository<DeliveryReadModel, String> {

    Optional<DeliveryReadModel> findByOrderId(String orderId);
}
