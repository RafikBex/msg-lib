package org.challenge.event;

import org.challenge.notification.api.Notification;
import org.challenge.notification.api.SendResult;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento publicado cuando una notificación fue enviada exitosamente.
 */
public record NotificationSentEvent(Notification notification, SendResult result,
                                     Instant occurredAt) implements NotificationEvent {

    public NotificationSentEvent {
        notification = Objects.requireNonNull(notification, "Notification cannot be null");
        result = Objects.requireNonNull(result, "Send result cannot be null");
        occurredAt = Objects.requireNonNullElseGet(occurredAt, Instant::now);

        if (!notification.id().equals(result.notificationId())) {
            throw new IllegalArgumentException("Notification ID and result notification ID must match");
        }
    }

    /**
     * Crea un evento con el momento actual.
     */
    public static NotificationSentEvent of(Notification notification, SendResult result) {
        return new NotificationSentEvent(notification, result, Instant.now());
    }
}