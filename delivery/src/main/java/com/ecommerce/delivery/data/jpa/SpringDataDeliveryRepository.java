package com.ecommerce.delivery.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataDeliveryRepository extends JpaRepository<DeliveryJpaEntity, String> {

    Optional<DeliveryJpaEntity> findByOrderId(String orderId);
}
