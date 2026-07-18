package org.challenge.notification.api;

import java.time.Instant;
import java.util.Map;

/**
 * Contrato base para cualquier notificación enviada a través de la librería.
 */
public interface Notification {

    /**
     * Identificador único de la notificación.
     */
    NotificationId id();

    /**
     * Momento en el que se creó la notificación.
     */
    Instant createdAt();

    /**
     * Metadatos adicionales asociados a la notificación.
     */
    Map<String, String> metadata();
}
