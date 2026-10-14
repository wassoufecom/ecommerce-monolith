package com.ecommerce.inventory.data.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataReservationItemRepository extends JpaRepository<ReservationItemJpaEntity, Long> {

    List<ReservationItemJpaEntity> findByReservationId(String reservationId);

    void deleteByReservationId(String reservationId);
}
