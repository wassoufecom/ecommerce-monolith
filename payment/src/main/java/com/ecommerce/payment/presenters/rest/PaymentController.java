package com.ecommerce.payment.presenters.rest;

import com.ecommerce.payment.queries.PaymentQueryService;
import com.ecommerce.payment.queries.PaymentQueryService.PaymentView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Payment — queries")
public class PaymentController {

    private final PaymentQueryService paymentQueryService;

    @Operation(summary = "Get the payment for an order (query side, Redis-cached)")
    @GetMapping("/payment/order/{orderId}")
    public PaymentView getPayment(@PathVariable String orderId) {
        return paymentQueryService.getPaymentByOrderId(orderId);
    }
}
