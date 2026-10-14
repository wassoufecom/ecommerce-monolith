package com.ecommerce.shared.domain;

/**
 * Payment details carried transiently through the saga.
 * NEVER persisted in plain text — only a token / last-4 is ever written to the DB.
 */
public record PaymentDetails(String cardNumber, String cvv, String expiryDate) {

    public PaymentDetails {
        if (cardNumber == null || !cardNumber.matches("\\d{13,19}")) {
            throw new DomainValidationException("card number must be 13-19 digits");
        }
        if (cvv == null || !cvv.matches("\\d{3,4}")) {
            throw new DomainValidationException("cvv must be 3-4 digits");
        }
        if (expiryDate == null || !expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new DomainValidationException("expiry date must be in MM/YY format");
        }
    }

    public String last4() {
        return cardNumber.substring(cardNumber.length() - 4);
    }
}
