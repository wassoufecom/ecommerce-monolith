package com.ecommerce.order.data.implementations;

import com.ecommerce.order.core.domain.Order;
import com.ecommerce.order.core.repositories.OrderRepository;
import com.ecommerce.order.data.jpa.SpringDataOrderRepository;
import com.ecommerce.order.data.mappers.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final SpringDataOrderRepository springData;

    @Override
    public Order save(Order order) {
        return OrderMapper.toDomain(springData.save(OrderMapper.toJpa(order)));
    }

    @Override
    public Optional<Order> findById(String id) {
        return springData.findById(id).map(OrderMapper::toDomain);
    }
}
