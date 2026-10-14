package com.ecommerce.delivery.core.repositories;

import com.ecommerce.delivery.core.domain.Delivery;

import java.util.Optional;

public interface DeliveryRepository {

    Delivery save(Delivery delivery);

    Optional<Delivery> findByOrderId(String orderId);
}
