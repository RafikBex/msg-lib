package org.challenge.model.email;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

/**
 * Dirección de correo electrónico validada.
 */
public record EmailAddress(String value) {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    public EmailAddress {
        Objects.requireNonNull(
                value,
                "Email address cannot be null"
        );

        value = value.trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    "Email address cannot be blank"
            );
        }

        if (!EMAIL_VALIDATOR.isValid(value)) {
            throw new IllegalArgumentException(
                    "Invalid email address: " + value
            );
        }
    }

    /**
     * Crea una dirección de email a partir de una cadena.
     */
    public static EmailAddress of(String value) {
        return new EmailAddress(value);
    }

    @Override
    public String toString() {
        return value;
    }
}