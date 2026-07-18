package org.challenge.notification.api;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador único de una notificación, basado en {@link UUID}.
 */
public record NotificationId(UUID value) {

    public NotificationId {
        Objects.requireNonNull(value, "Notification ID cannot be null");
    }

    /**
     * Genera un nuevo identificador aleatorio.
     */
    public static NotificationId generate() {
        return new NotificationId(UUID.randomUUID());
    }

    /**
     * Crea un identificador a partir de su representación textual.
     *
     * @throws IllegalArgumentException si el valor no es un UUID válido
     */
    public static NotificationId from(String value) {
        Objects.requireNonNull(value, "Notification ID cannot be null");

        try {
            return new NotificationId(UUID.fromString(value));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid notification ID: " + value,
                    exception
            );
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}