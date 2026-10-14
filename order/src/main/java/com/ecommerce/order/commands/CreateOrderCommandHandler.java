package com.ecommerce.order.commands;

import com.ecommerce.order.core.usecases.CreateOrderUseCase;
import com.ecommerce.shared.domain.OrderLine;
import com.ecommerce.shared.domain.PaymentDetails;
import com.ecommerce.shared.domain.ShippingAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateOrderCommandHandler {

    private final CreateOrderUseCase createOrderUseCase;

    public String handle(CreateOrderCommand command) {
        List<OrderLine> items = command.orderItems().stream()
                .map(item -> new OrderLine(item.productId(), item.quantity(), item.price()))
                .toList();
        ShippingAddress address = new ShippingAddress(command.address().street(),
                command.address().city(), command.address().zip(), command.address().country());
        PaymentDetails paymentDetails = new PaymentDetails(command.paymentDetails().cardNumber(),
                command.paymentDetails().cvv(), command.paymentDetails().expiryDate());

        return createOrderUseCase.execute(command.userId(), items, command.price(),
                address, paymentDetails);
    }
}
