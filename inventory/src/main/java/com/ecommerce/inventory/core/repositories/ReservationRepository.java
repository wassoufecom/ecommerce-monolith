package com.ecommerce.inventory.core.repositories;

import com.ecommerce.inventory.core.domain.Reservation;

import java.util.Optional;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    Optional<Reservation> findByOrderId(String orderId);
}
