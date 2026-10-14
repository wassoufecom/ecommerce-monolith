package com.ecommerce.catalog.commands;

import com.ecommerce.catalog.core.usecases.CreateProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateProductCommandHandler {

    private final CreateProductUseCase createProductUseCase;

    public String handle(CreateProductCommand command) {
        return createProductUseCase.execute(command.title(), command.description(),
                command.price(), command.categories());
    }
}
