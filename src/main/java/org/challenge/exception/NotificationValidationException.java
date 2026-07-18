package org.challenge.exception;

import org.challenge.notification.api.NotificationId;

import java.io.Serial;
import java.util.Optional;

/**
 * Indica que una notificación contiene datos inválidos.
 */
public final class NotificationValidationException extends NotificationException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String field;

    public NotificationValidationException(String message) {
        this(null, null, message, null);
    }

    public NotificationValidationException(
            String message,
            Throwable cause
    ) {
        this(null, null, message, cause);
    }

    public NotificationValidationException(
            NotificationId notificationId,
            String message
    ) {
        this(notificationId, null, message, null);
    }

    public NotificationValidationException(
            NotificationId notificationId,
            String message,
            Throwable cause
    ) {
        this(notificationId, null, message, cause);
    }

    public NotificationValidationException(
            NotificationId notificationId,
            String field,
            String message
    ) {
        this(notificationId, field, message, null);
    }

    public NotificationValidationException(
            NotificationId notificationId,
            String field,
            String message,
            Throwable cause
    ) {
        super(
                NotificationErrorCode.VALIDATION_ERROR,
                notificationId,
                buildMessage(field, message),
                cause
        );

        this.field = normalizeField(field);
    }

    /**
     * Campo que causó el error de validación, si aplica.
     */
    public Optional<String> field() {
        return Optional.ofNullable(field);
    }

    /**
     * Crea una excepción de validación para un campo específico.
     */
    public static NotificationValidationException forField(
            NotificationId notificationId,
            String field,
            String message
    ) {
        return new NotificationValidationException(
                notificationId,
                field,
                message
        );
    }

    private static String buildMessage(
            String field,
            String message
    ) {
        if (field == null || field.isBlank()) {
            return message;
        }

        return "Invalid field '%s': %s".formatted(
                field.trim(),
                message
        );
    }

    private static String normalizeField(String field) {
        if (field == null || field.isBlank()) {
            return null;
        }

        return field.trim();
    }
}