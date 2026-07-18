package org.challenge.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Indica que la librería no tiene registrado un pipeline
 * para el tipo de notificación recibido.
 */
public final class UnsupportedNotificationException
        extends NotificationException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Class<?> notificationType;

    public UnsupportedNotificationException(
            Class<?> notificationType
    ) {
        super(
                NotificationErrorCode.UNSUPPORTED_NOTIFICATION,
                buildMessage(notificationType)
        );

        this.notificationType = Objects.requireNonNull(
                notificationType,
                "Notification type cannot be null"
        );
    }

    /**
     * Tipo de notificación que no tiene pipeline registrado.
     */
    public Class<?> notificationType() {
        return notificationType;
    }

    private static String buildMessage(
            Class<?> notificationType
    ) {
        Objects.requireNonNull(
                notificationType,
                "Notification type cannot be null"
        );

        return "No channel pipeline is registered for notification type '%s'"
                .formatted(notificationType.getName());
    }
}