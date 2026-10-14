package com.ecommerce.order.commands;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * Command DTO for POST /order/create.
 * price must match the sum of item.price * quantity (validated against catalog items).
 */
public record CreateOrderCommand(
        @NotBlank(message = "userId must not be blank") String userId,
        @Valid @NotNull(message = "address is required") AddressDto address,
        @NotEmpty(message = "order must contain at least one item") @Valid List<OrderItemDto> orderItems,
        @NotNull(message = "price is required") @Positive(message = "price must be positive") BigDecimal price,
        @Valid @NotNull(message = "paymentDetails are required") PaymentDetailsDto paymentDetails
) {

    public record AddressDto(
            @NotBlank(message = "street must not be blank") String street,
            @NotBlank(message = "city must not be blank") String city,
            @NotBlank(message = "zip must not be blank") String zip,
            @NotBlank(message = "country must not be blank") String country
    ) {
    }

    public record OrderItemDto(
            @NotBlank(message = "productId must not be blank") String productId,
            @Positive(message = "quantity must be >= 1") int quantity,
            @NotNull(message = "price must not be null") @Positive(message = "price must be positive") BigDecimal price
    ) {
    }

    public record PaymentDetailsDto(
            @NotBlank(message = "cardNumber must not be blank") String cardNumber,
            @NotBlank(message = "cvv must not be blank") String cvv,
            @NotBlank(message = "expiryDate must not be blank") String expiryDate
    ) {
    }
}
