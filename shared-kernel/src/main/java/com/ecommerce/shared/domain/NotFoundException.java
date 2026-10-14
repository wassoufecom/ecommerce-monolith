package com.ecommerce.shared.domain;

/**
 * Thrown when a requested read resource does not exist (maps to HTTP 404).
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
