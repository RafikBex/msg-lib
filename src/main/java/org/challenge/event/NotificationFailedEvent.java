package org.challenge.event;


import org.challenge.exception.NotificationException;
import org.challenge.notification.api.Notification;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento publicado cuando el envío de una notificación falla.
 */
public record NotificationFailedEvent(Notification notification, NotificationException exception,
                                       Instant occurredAt) implements NotificationEvent {

    public NotificationFailedEvent {
        notification = Objects.requireNonNull(notification, "Notification cannot be null");
        exception = Objects.requireNonNull(exception, "Notification exception cannot be null");
        occurredAt = Objects.requireNonNullElseGet(occurredAt, Instant::now);
    }

    /**
     * Crea un evento con el momento actual.
     */
    public static NotificationFailedEvent of(Notification notification, NotificationException exception) {
        return new NotificationFailedEvent(notification, exception, Instant.now());
    }
}