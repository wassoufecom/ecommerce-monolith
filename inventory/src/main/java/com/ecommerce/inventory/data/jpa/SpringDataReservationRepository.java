package com.ecommerce.inventory.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataReservationRepository extends JpaRepository<ReservationJpaEntity, String> {

    Optional<ReservationJpaEntity> findByOrderId(String orderId);
}
