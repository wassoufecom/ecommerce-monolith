package com.ecommerce.delivery.data.implementations;

import com.ecommerce.delivery.core.domain.Delivery;
import com.ecommerce.delivery.core.repositories.DeliveryRepository;
import com.ecommerce.delivery.data.jpa.DeliveryJpaEntity;
import com.ecommerce.delivery.data.jpa.SpringDataDeliveryRepository;
import com.ecommerce.shared.domain.ShippingAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeliveryRepositoryImpl implements DeliveryRepository {

    private final SpringDataDeliveryRepository springData;

    @Override
    public Delivery save(Delivery delivery) {
        springData.save(toJpa(delivery));
        return delivery;
    }

    @Override
    public Optional<Delivery> findByOrderId(String orderId) {
        return springData.findByOrderId(orderId).map(DeliveryRepositoryImpl::toDomain);
    }

    private static DeliveryJpaEntity toJpa(Delivery delivery) {
        DeliveryJpaEntity entity = new DeliveryJpaEntity();
        entity.setId(delivery.getId());
        entity.setOrderId(delivery.getOrderId());
        entity.setStatus(delivery.getStatus());
        entity.setStreet(delivery.getAddress().street());
        entity.setCity(delivery.getAddress().city());
        entity.setZip(delivery.getAddress().zip());
        entity.setCountry(delivery.getAddress().country());
        entity.setCreatedAt(Instant.now());
        return entity;
    }

    private static Delivery toDomain(DeliveryJpaEntity entity) {
        return Delivery.reconstitute(entity.getId(), entity.getOrderId(),
                new ShippingAddress(entity.getStreet(), entity.getCity(), entity.getZip(),
                        entity.getCountry()),
                entity.getStatus());
    }
}
