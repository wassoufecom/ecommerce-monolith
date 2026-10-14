package com.ecommerce.inventory.commands;

import com.ecommerce.inventory.core.usecases.AddStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddStockCommandHandler {

    private final AddStockUseCase addStockUseCase;

    public void handle(AddStockCommand command) {
        addStockUseCase.execute(command.productId(), command.quantity());
    }
}
