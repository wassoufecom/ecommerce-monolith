package com.ecommerce.order.presenters.handlers;

import com.ecommerce.order.queries.OrderReadModel;
import com.ecommerce.order.queries.SpringDataOrderReadRepository;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.events.DeliveryScheduled;
import com.ecommerce.shared.domain.events.OrderCreated;
import com.ecommerce.shared.domain.events.PaymentCreated;
import com.ecommerce.shared.domain.events.PaymentFailed;
import com.ecommerce.shared.domain.events.StockCheckFailed;
import com.ecommerce.shared.domain.events.StockCheckedAndReserved;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

/**
 * Projection updater: keeps order_read_model in sync with transient domain events.
 * Runs in the producer's transaction (in-tx with the write that generated the event).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderProjectionUpdater {

    private final SpringDataOrderReadRepository readRepository;
    private final ObjectMapper objectMapper;

    @EventListener
    public void on(OrderCreated event) {
        OrderReadModel readModel = new OrderReadModel();
        readModel.setId(event.orderId());
        readModel.setUserId(event.userId());
        readModel.setStatus("PENDING");
        readModel.setTotalAmount(event.totalAmount());
        readModel.setItems(toJson(event.items()));
        readModel.setStreet(event.address().street());
        readModel.setCity(event.address().city());
        readModel.setZip(event.address().zip());
        readModel.setCountry(event.address().country());
        readModel.setCreatedAt(Instant.now());
        readRepository.save(readModel);
        log.info("Order projection: order_read_model created [orderId={}]", event.orderId());
    }

    @EventListener
    public void on(StockCheckedAndReserved event) {
        updateStatus(event.orderId(), "STOCK_RESERVED");
    }

    @EventListener
    public void on(StockCheckFailed event) {
        updateStatus(event.orderId(), "REJECTED");
    }

    @EventListener
    public void on(PaymentCreated event) {
        updateStatus(event.orderId(), "PAYMENT_COMPLETED");
    }

    @EventListener
    public void on(PaymentFailed event) {
        updateStatus(event.orderId(), "PAYMENT_FAILED");
    }

    @EventListener
    public void on(DeliveryScheduled event) {
        updateStatus(event.orderId(), "CONFIRMED");
    }

    private void updateStatus(String orderId, String status) {
        readRepository.findById(orderId).ifPresent(readModel -> {
            readModel.setStatus(status);
            readRepository.save(readModel);
            log.info("Order projection: status updated [orderId={}, status={}]", orderId, status);
        });
    }

    private String toJson(List<OrderLine> items) {
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("cannot serialize order items", e);
        }
    }
}
