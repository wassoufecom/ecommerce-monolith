package com.ecommerce.catalog.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductCommand(
        @NotBlank(message = "title must not be blank") String title,
        String description,
        @Positive(message = "price must be positive") BigDecimal price,
        List<String> categories
) {
}
