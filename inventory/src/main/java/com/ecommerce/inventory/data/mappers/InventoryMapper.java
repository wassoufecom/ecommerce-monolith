package com.ecommerce.inventory.data.mappers;

import com.ecommerce.inventory.core.domain.Reservation;
import com.ecommerce.inventory.core.domain.Stock;
import com.ecommerce.inventory.data.jpa.InventoryJpaEntity;
import com.ecommerce.inventory.data.jpa.ReservationItemJpaEntity;
import com.ecommerce.inventory.data.jpa.ReservationJpaEntity;
import com.ecommerce.shared.domain.OrderLine;

import java.util.List;

public final class InventoryMapper {

    private InventoryMapper() {
    }

    public static InventoryJpaEntity toJpa(Stock stock) {
        InventoryJpaEntity entity = new InventoryJpaEntity();
        entity.setProductId(stock.getProductId());
        entity.setAvailableQty(stock.getAvailableQty());
        return entity;
    }

    public static Stock toDomain(InventoryJpaEntity entity) {
        return Stock.of(entity.getProductId(), entity.getAvailableQty());
    }

    public static ReservationJpaEntity toJpa(Reservation reservation) {
        ReservationJpaEntity entity = new ReservationJpaEntity();
        entity.setId(reservation.getId());
        entity.setOrderId(reservation.getOrderId());
        entity.setStatus(ReservationJpaEntity.Status.valueOf(reservation.getStatus().name()));
        return entity;
    }

    public static Reservation toDomain(ReservationJpaEntity entity, List<OrderLine> lines) {
        return Reservation.reconstitute(entity.getId(), entity.getOrderId(), lines,
                Reservation.Status.valueOf(entity.getStatus().name()));
    }

    public static ReservationItemJpaEntity toJpa(String reservationId, OrderLine line) {
        ReservationItemJpaEntity entity = new ReservationItemJpaEntity();
        entity.setReservationId(reservationId);
        entity.setProductId(line.productId());
        entity.setQuantity(line.quantity());
        entity.setUnitPrice(line.unitPrice());
        return entity;
    }

    public static OrderLine orderLine(ReservationItemJpaEntity item) {
        return new OrderLine(item.getProductId(), item.getQuantity(), item.getUnitPrice());
    }
}
