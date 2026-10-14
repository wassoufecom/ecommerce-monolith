package com.ecommerce.delivery.core.repositories;

import com.ecommerce.delivery.core.domain.OrderAddressSnapshot;

import java.util.Optional;

public interface OrderAddressRepository {

    OrderAddressSnapshot save(OrderAddressSnapshot snapshot);

    Optional<OrderAddressSnapshot> findByOrderId(String orderId);
}
