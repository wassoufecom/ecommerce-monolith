package com.ecommerce.inventory.commands;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddStockCommand(
        @NotBlank(message = "productId must not be blank") String productId,
        @Min(value = 1, message = "quantity must be >= 1") int quantity
) {
}
