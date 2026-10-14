package com.ecommerce.catalog.commands;

import com.ecommerce.catalog.core.usecases.CreateCategoryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateCategoryCommandHandler {

    private final CreateCategoryUseCase createCategoryUseCase;

    public String handle(CreateCategoryCommand command) {
        return createCategoryUseCase.execute(command.name());
    }
}
