package com.ecommerce.delivery.presenters.rest;

import com.ecommerce.delivery.queries.DeliveryQueryService;
import com.ecommerce.delivery.queries.DeliveryQueryService.DeliveryView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Delivery — queries")
public class DeliveryController {

    private final DeliveryQueryService deliveryQueryService;

    @Operation(summary = "Get the delivery for an order (query side, Redis-cached)")
    @GetMapping("/delivery/order/{orderId}")
    public DeliveryView getDelivery(@PathVariable String orderId) {
        return deliveryQueryService.getDeliveryByOrderId(orderId);
    }
}
