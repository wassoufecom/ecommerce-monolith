package com.ecommerce.order.data.mappers;

import com.ecommerce.order.core.domain.Order;
import com.ecommerce.order.data.jpa.OrderItemJpaEntity;
import com.ecommerce.order.data.jpa.OrderJpaEntity;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.ShippingAddress;

import java.util.ArrayList;
import java.util.List;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderJpaEntity toJpa(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getId());
        entity.setUserId(order.getUserId());
        entity.setStatus(order.getStatus());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setStreet(order.getAddress().street());
        entity.setCity(order.getAddress().city());
        entity.setZip(order.getAddress().zip());
        entity.setCountry(order.getAddress().country());

        List<OrderItemJpaEntity> items = new ArrayList<>();
        for (OrderLine line : order.getItems()) {
            OrderItemJpaEntity item = new OrderItemJpaEntity();
            item.setOrder(entity);
            item.setProductId(line.productId());
            item.setQuantity(line.quantity());
            item.setUnitPrice(line.unitPrice());
            items.add(item);
        }
        entity.setItems(items);
        return entity;
    }

    public static Order toDomain(OrderJpaEntity entity) {
        List<OrderLine> items = entity.getItems().stream()
                .map(item -> new OrderLine(item.getProductId(), item.getQuantity(), item.getUnitPrice()))
                .toList();
        return Order.reconstitute(entity.getId(), entity.getUserId(), items, entity.getTotalAmount(),
                new ShippingAddress(entity.getStreet(), entity.getCity(), entity.getZip(),
                        entity.getCountry()),
                entity.getStatus());
    }
}
