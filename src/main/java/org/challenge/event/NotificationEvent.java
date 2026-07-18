package org.challenge.event;

import org.challenge.notification.api.Notification;
import org.challenge.notification.api.NotificationId;

import java.time.Instant;

/**
 * Evento generado durante el ciclo de vida de una notificación.
 */
public sealed interface NotificationEvent
        permits NotificationSentEvent, NotificationFailedEvent {

    /**
     * Notificación asociada al evento.
     */
    Notification notification();

    /**
     * Momento en el que ocurrió el evento.
     */
    Instant occurredAt();

    /**
     * Identificador de la notificación asociada.
     */
    default NotificationId notificationId() {
        return notification().id();
    }
}