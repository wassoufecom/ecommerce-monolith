package com.ecommerce.inventory.data.implementations;

import com.ecommerce.inventory.core.domain.Reservation;
import com.ecommerce.inventory.core.repositories.ReservationRepository;
import com.ecommerce.inventory.data.jpa.ReservationItemJpaEntity;
import com.ecommerce.inventory.data.jpa.SpringDataReservationItemRepository;
import com.ecommerce.inventory.data.jpa.SpringDataReservationRepository;
import com.ecommerce.inventory.data.mappers.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationRepositoryImpl implements ReservationRepository {

    private final SpringDataReservationRepository springData;
    private final SpringDataReservationItemRepository itemSpringData;

    @Override
    public Reservation save(Reservation reservation) {
        // Parent first — reservation_items has an FK to reservations.
        springData.save(InventoryMapper.toJpa(reservation));
        itemSpringData.deleteByReservationId(reservation.getId());
        itemSpringData.flush();
        reservation.getItems().forEach(line ->
                itemSpringData.save(InventoryMapper.toJpa(reservation.getId(), line)));
        return InventoryMapper.toDomain(springData.findById(reservation.getId()).orElseThrow(),
                reservation.getItems());
    }

    @Override
    public Optional<Reservation> findByOrderId(String orderId) {
        return springData.findByOrderId(orderId)
                .map(entity -> {
                    List<com.ecommerce.shared.domain.OrderLine> lines =
                            itemSpringData.findByReservationId(entity.getId()).stream()
                                    .map(InventoryMapper::orderLine)
                                    .toList();
                    return InventoryMapper.toDomain(entity, lines);
                });
    }
}
