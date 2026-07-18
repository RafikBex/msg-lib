package org.challenge.exception;


import org.challenge.notification.api.NotificationId;

import java.io.Serial;
import java.util.Objects;

/**
 * Excepción producida cuando un proveedor no puede enviar
 * o procesar una notificación.
 *
 * Un error puede ser:
 * - Reintentable: timeout, rate limit o servicio no disponible.
 * - No reintentable: credenciales inválidas o destinatario rechazado.
 */
public final class NotificationDeliveryException
        extends NotificationException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String providerName;
    private final boolean retryable;

    public NotificationDeliveryException(
            String providerName,
            NotificationId notificationId,
            String message
    ) {
        this(
                providerName,
                notificationId,
                message,
                false,
                null
        );
    }

    public NotificationDeliveryException(
            String providerName,
            NotificationId notificationId,
            String message,
            Throwable cause
    ) {
        this(
                providerName,
                notificationId,
                message,
                false,
                cause
        );
    }

    public NotificationDeliveryException(
            String providerName,
            NotificationId notificationId,
            String message,
            boolean retryable
    ) {
        this(
                providerName,
                notificationId,
                message,
                retryable,
                null
        );
    }

    public NotificationDeliveryException(
            String providerName,
            NotificationId notificationId,
            String message,
            boolean retryable,
            Throwable cause
    ) {
        super(
                NotificationErrorCode.DELIVERY_ERROR,
                notificationId,
                buildMessage(providerName, message),
                cause
        );

        this.providerName = normalizeProviderName(providerName);
        this.retryable = retryable;
    }

    /**
     * Construye una excepción para errores temporales que
     * pueden volver a intentarse.
     */
    public static NotificationDeliveryException retryable(
            String providerName,
            NotificationId notificationId,
            String message,
            Throwable cause
    ) {
        return new NotificationDeliveryException(
                providerName,
                notificationId,
                message,
                true,
                cause
        );
    }

    /**
     * Construye una excepción para errores definitivos que
     * no deberían volver a intentarse.
     */
    public static NotificationDeliveryException nonRetryable(
            String providerName,
            NotificationId notificationId,
            String message,
            Throwable cause
    ) {
        return new NotificationDeliveryException(
                providerName,
                notificationId,
                message,
                false,
                cause
        );
    }

    /**
     * Nombre del proveedor que produjo el error de entrega.
     */
    public String providerName() {
        return providerName;
    }

    /**
     * Indica si el error permite reintentar el envío.
     */
    public boolean isRetryable() {
        return retryable;
    }

    private static String buildMessage(
            String providerName,
            String message
    ) {
        String normalizedProvider =
                normalizeProviderName(providerName);

        String normalizedMessage =
                normalizeMessage(message);

        return "Provider '%s': %s".formatted(
                normalizedProvider,
                normalizedMessage
        );
    }

    private static String normalizeProviderName(
            String providerName
    ) {
        Objects.requireNonNull(
                providerName,
                "Provider name cannot be null"
        );

        String normalized = providerName.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "Provider name cannot be blank"
            );
        }

        return normalized;
    }

    private static String normalizeMessage(String message) {
        Objects.requireNonNull(
                message,
                "Delivery error message cannot be null"
        );

        String normalized = message.trim();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "Delivery error message cannot be blank"
            );
        }

        return normalized;
    }
}