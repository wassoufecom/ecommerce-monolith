package com.ecommerce.order.core.repositories;

import com.ecommerce.order.core.domain.Order;

import java.util.Optional;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(String id);
}
