package com.ecommerce.delivery.core.usecases;

import com.ecommerce.delivery.core.domain.Delivery;
import com.ecommerce.delivery.core.repositories.DeliveryRepository;
import com.ecommerce.delivery.core.repositories.OrderAddressRepository;
import com.ecommerce.shared.domain.DomainValidationException;
import com.ecommerce.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ScheduleDelivery: triggered by PaymentCreated (payment is COMPLETED by definition).
 * Uses the locally stored address snapshot. Exactly one delivery per order.
 *
 * This step only persists the aggregate — publishing DeliveryScheduled is done by the
 * saga handler AFTER this transaction commits, so the query projections read committed state.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleDeliveryUseCase {

    private final DeliveryRepository deliveryRepository;
    private final OrderAddressRepository orderAddressRepository;

    @Transactional
    public Delivery execute(String orderId) {
        if (deliveryRepository.findByOrderId(orderId).isPresent()) {
            log.warn("Delivery: active delivery for order {} already exists — skipping (idempotent)", orderId);
            return null;
        }

        var snapshot = orderAddressRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DomainValidationException(
                        "no shipping address known for order " + orderId));
        if (snapshot.getAddress() == null) {
            throw new NotFoundException("shipping address missing for order " + orderId);
        }

        Delivery delivery = Delivery.schedule(orderId, snapshot.getAddress());
        deliveryRepository.save(delivery);
        log.info("Delivery: scheduled [orderId={}, deliveryId={}, city={}]",
                orderId, delivery.getId(), snapshot.getAddress().city());
        return delivery;
    }
}
