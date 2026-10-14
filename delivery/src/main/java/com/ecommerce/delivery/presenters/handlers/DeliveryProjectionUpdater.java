package com.ecommerce.delivery.presenters.handlers;

import com.ecommerce.delivery.queries.DeliveryReadModel;
import com.ecommerce.delivery.queries.SpringDataDeliveryReadRepository;
import com.ecommerce.shared.domain.events.DeliveryScheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Projection updater: keeps delivery_read_model in sync with transient domain events.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryProjectionUpdater {

    private final SpringDataDeliveryReadRepository readRepository;

    @EventListener
    public void on(DeliveryScheduled event) {
        DeliveryReadModel readModel = new DeliveryReadModel();
        readModel.setId(java.util.UUID.randomUUID().toString());
        readModel.setOrderId(event.orderId());
        readModel.setStatus("SCHEDULED");
        readModel.setStreet(event.address().street());
        readModel.setCity(event.address().city());
        readModel.setZip(event.address().zip());
        readModel.setCountry(event.address().country());
        readModel.setCreatedAt(Instant.now());
        readRepository.save(readModel);
        log.info("Delivery projection: delivery_read_model updated [orderId={}, status=SCHEDULED]",
                event.orderId());
    }
}
