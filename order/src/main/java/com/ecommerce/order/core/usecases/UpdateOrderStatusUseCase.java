package com.ecommerce.order.core.usecases;

import com.ecommerce.order.core.domain.Order;
import com.ecommerce.order.core.domain.OrderStatus;
import com.ecommerce.order.core.repositories.OrderRepository;
import com.ecommerce.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

/**
 * Applies saga-driven status transitions to the order aggregate.
 * Each method is guarded so terminal states are not overwritten.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateOrderStatusUseCase {

    private final OrderRepository orderRepository;

    @Transactional
    public void markStockReserved(String orderId) {
        transition(orderId, order -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.markStockReserved();
            }
        }, "stock reserved");
    }

    @Transactional
    public void markPaymentCompleted(String orderId) {
        transition(orderId, order -> {
            if (order.getStatus() == OrderStatus.STOCK_RESERVED) {
                order.markPaymentCompleted();
            }
        }, "payment completed");
    }

    @Transactional
    public void markConfirmed(String orderId) {
        transition(orderId, order -> {
            if (order.getStatus() == OrderStatus.PAYMENT_COMPLETED) {
                order.markConfirmed();
            }
        }, "delivery scheduled — order confirmed");
    }

    @Transactional
    public void reject(String orderId) {
        transition(orderId, order -> {
            if (order.getStatus() == OrderStatus.PENDING) {
                order.reject();
            }
        }, "rejected (stock check failed)");
    }

    @Transactional
    public void markPaymentFailed(String orderId) {
        transition(orderId, order -> {
            if (order.getStatus() == OrderStatus.STOCK_RESERVED || order.getStatus() == OrderStatus.PENDING) {
                order.markPaymentFailed();
            }
        }, "payment failed");
    }

    private void transition(String orderId, Consumer<Order> change, String description) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("order " + orderId + " not found"));
        change.accept(order);
        orderRepository.save(order);
        log.info("Order: status transition [orderId={}, status={}, reason={}]",
                orderId, order.getStatus(), description);
    }
}
