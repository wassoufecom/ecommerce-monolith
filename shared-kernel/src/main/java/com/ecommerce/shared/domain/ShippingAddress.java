package com.ecommerce.shared.domain;

/**
 * Shipping address value object (street, city, ZIP, country are all mandatory).
 */
public record ShippingAddress(String street, String city, String zip, String country) {

    public ShippingAddress {
        require(street, "street");
        require(city, "city");
        require(zip, "zip");
        require(country, "country");
    }

    private static void require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException("shipping address " + field + " must not be blank");
        }
    }
}
