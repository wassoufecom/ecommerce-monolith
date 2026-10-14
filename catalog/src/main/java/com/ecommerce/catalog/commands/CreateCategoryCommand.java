package com.ecommerce.catalog.commands;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryCommand(
        @NotBlank(message = "name must not be blank") String name
) {
}
