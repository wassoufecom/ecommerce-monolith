package com.ecommerce.shared.domain;

/**
 * Thrown when a domain invariant is violated (maps to HTTP 422).
 */
public class DomainValidationException extends RuntimeException {

    public DomainValidationException(String message) {
        super(message);
    }
}
