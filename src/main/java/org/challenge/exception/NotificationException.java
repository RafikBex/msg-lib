package org.challenge.exception;

import org.challenge.notification.api.NotificationId;

import java.io.Serial;
import java.util.Objects;
import java.util.Optional;

/**
 * Excepción base para todos los errores controlados
 * producidos por la librería de notificaciones.
 */
public abstract class NotificationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final NotificationErrorCode errorCode;
    private final NotificationId notificationId;

    protected NotificationException(
            NotificationErrorCode errorCode,
            String message
    ) {
        this(errorCode, null, message, null);
    }

    protected NotificationException(
            NotificationErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        this(errorCode, null, message, cause);
    }

    protected NotificationException(
            NotificationErrorCode errorCode,
            NotificationId notificationId,
            String message
    ) {
        this(errorCode, notificationId, message, null);
    }

    protected NotificationException(
            NotificationErrorCode errorCode,
            NotificationId notificationId,
            String message,
            Throwable cause
    ) {
        super(requireMessage(message), cause);

        this.errorCode = Objects.requireNonNull(
                errorCode,
                "Error code cannot be null"
        );

        this.notificationId = notificationId;
    }

    /**
     * Código de error asociado a la excepción.
     */
    public NotificationErrorCode errorCode() {
        return errorCode;
    }

    /**
     * Identificador de la notificación que causó el error, si está disponible.
     */
    public Optional<NotificationId> notificationId() {
        return Optional.ofNullable(notificationId);
    }

    private static String requireMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException(
                    "Exception message cannot be null or blank"
            );
        }

        return message;
    }
}