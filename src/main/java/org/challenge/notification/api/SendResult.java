package org.challenge.notification.api;

import java.time.Instant;

/**
 * Resultado del envío de una notificación.
 */
public record SendResult(
        NotificationId notificationId,
        String provider,
        String providerMessageId,
        DeliveryStatus status,
        Instant processedAt
) {

    //TODO: faltan factory methods para los estados ACCEPTED y FAILED definidos en DeliveryStatus.

    /**
     * Crea un resultado con estado {@link DeliveryStatus#SENT}.
     */
    public static SendResult sent(
            NotificationId notificationId,
            String provider,
            String providerMessageId
    ) {
        return new SendResult(
                notificationId,
                provider,
                providerMessageId,
                DeliveryStatus.SENT,
                Instant.now()
        );
    }
}