package org.challenge.model.push;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Value Object que representa el token de un dispositivo
 * para notificaciones Push.
 * <p>
 * El formato concreto depende del proveedor, por ejemplo:
 * Firebase Cloud Messaging o Apple Push Notification Service.
 */
public record DeviceToken(String value) {

    /*
     * Se utiliza un límite amplio porque algunos proveedores
     * generan tokens extensos.
     */
    private static final int MAX_TOKEN_LENGTH = 4096;

    public DeviceToken {
        Objects.requireNonNull(
                value,
                "Device token cannot be null"
        );

        value = value.trim();

        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(
                    "Device token cannot be blank"
            );
        }

        if (value.length() > MAX_TOKEN_LENGTH) {
            throw new IllegalArgumentException(
                    "Device token cannot exceed "
                            + MAX_TOKEN_LENGTH
                            + " characters"
            );
        }

        if (containsWhitespace(value)) {
            throw new IllegalArgumentException(
                    "Device token cannot contain whitespace"
            );
        }

        if (containsControlCharacters(value)) {
            throw new IllegalArgumentException(
                    "Device token cannot contain control characters"
            );
        }
    }

    /**
     * Crea un token de dispositivo a partir de una cadena.
     */
    public static DeviceToken of(String value) {
        return new DeviceToken(value);
    }

    private static boolean containsWhitespace(String value) {
        return value.chars()
                .anyMatch(Character::isWhitespace);
    }

    private static boolean containsControlCharacters(String value) {
        return value.chars()
                .anyMatch(Character::isISOControl);
    }

    @Override
    public String toString() {
        return value;
    }
}