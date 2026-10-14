package com.ecommerce.delivery.core.usecases;

import com.ecommerce.delivery.core.domain.OrderAddressSnapshot;
import com.ecommerce.delivery.core.repositories.OrderAddressRepository;
import com.ecommerce.shared.domain.ShippingAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Stores the order's shipping address (from OrderCreated) so delivery scheduling
 * never has to query the order database.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RememberOrderAddressUseCase {

    private final OrderAddressRepository orderAddressRepository;

    @Transactional
    public void execute(String orderId, String userId, ShippingAddress address) {
        if (orderAddressRepository.findByOrderId(orderId).isPresent()) {
            return; // idempotent
        }
        orderAddressRepository.save(OrderAddressSnapshot.of(orderId, userId, address));
        log.info("Delivery: address snapshot stored [orderId={}, city={}]", orderId, address.city());
    }
}
