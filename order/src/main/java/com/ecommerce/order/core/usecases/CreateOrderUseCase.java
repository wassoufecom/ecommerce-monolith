package com.ecommerce.order.core.usecases;

import com.ecommerce.order.core.domain.Order;
import com.ecommerce.order.core.repositories.OrderRepository;
import com.ecommerce.shared.domain.EventPublisher;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.PaymentDetails;
import com.ecommerce.shared.domain.ShippingAddress;
import com.ecommerce.shared.domain.events.OrderCreated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * CreateOrder: persist the order aggregate and emit OrderCreated (saga step 1).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;

    @Transactional
    public String execute(String userId, List<OrderLine> items, BigDecimal total,
                          ShippingAddress address, PaymentDetails paymentDetails) {
        Order order = Order.create(userId, items, total, address);
        orderRepository.save(order);
        log.info("Order: created [orderId={}, userId={}, total={}, items={}]",
                order.getId(), userId, total, items.size());

        eventPublisher.publish(OrderCreated.of(order.getId(), userId, order.getItems(),
                order.getTotalAmount(), address, paymentDetails));
        return order.getId();
    }
}
