package com.ecommerce.delivery.data.implementations;

import com.ecommerce.delivery.core.domain.OrderAddressSnapshot;
import com.ecommerce.delivery.core.repositories.OrderAddressRepository;
import com.ecommerce.delivery.data.jpa.OrderAddressSnapshotJpaEntity;
import com.ecommerce.delivery.data.jpa.SpringDataOrderAddressSnapshotRepository;
import com.ecommerce.shared.domain.ShippingAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderAddressRepositoryImpl implements OrderAddressRepository {

    private final SpringDataOrderAddressSnapshotRepository springData;

    @Override
    public OrderAddressSnapshot save(OrderAddressSnapshot snapshot) {
        springData.save(toJpa(snapshot));
        return snapshot;
    }

    @Override
    public Optional<OrderAddressSnapshot> findByOrderId(String orderId) {
        return springData.findById(orderId).map(OrderAddressRepositoryImpl::toDomain);
    }

    private static OrderAddressSnapshotJpaEntity toJpa(OrderAddressSnapshot snapshot) {
        OrderAddressSnapshotJpaEntity entity = new OrderAddressSnapshotJpaEntity();
        entity.setOrderId(snapshot.getOrderId());
        entity.setUserId(snapshot.getUserId());
        entity.setStreet(snapshot.getAddress().street());
        entity.setCity(snapshot.getAddress().city());
        entity.setZip(snapshot.getAddress().zip());
        entity.setCountry(snapshot.getAddress().country());
        return entity;
    }

    private static OrderAddressSnapshot toDomain(OrderAddressSnapshotJpaEntity entity) {
        return OrderAddressSnapshot.reconstitute(entity.getOrderId(), entity.getUserId(),
                new ShippingAddress(entity.getStreet(), entity.getCity(), entity.getZip(),
                        entity.getCountry()));
    }
}
