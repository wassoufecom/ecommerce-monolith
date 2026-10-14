package com.ecommerce.delivery.queries;

import com.ecommerce.shared.domain.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Query side of the delivery context — reads from delivery_read_model only.
 */
@Service
@RequiredArgsConstructor
public class DeliveryQueryService {

    public record DeliveryView(String id, String orderId, String status, String street, String city,
                               String zip, String country, Instant createdAt) implements java.io.Serializable {
    }

    private final SpringDataDeliveryReadRepository readRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "delivery", key = "#orderId")
    public DeliveryView getDeliveryByOrderId(String orderId) {
        return readRepository.findByOrderId(orderId)
                .map(m -> new DeliveryView(m.getId(), m.getOrderId(), m.getStatus(), m.getStreet(),
                        m.getCity(), m.getZip(), m.getCountry(), m.getCreatedAt()))
                .orElseThrow(() -> new NotFoundException("no delivery for order " + orderId));
    }
}
