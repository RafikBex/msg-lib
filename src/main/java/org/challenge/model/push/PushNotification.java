package org.challenge.model.push;

import org.challenge.notification.api.Notification;
import org.challenge.notification.api.NotificationId;

import java.time.Instant;
import java.util.Map;

/**
 * Notificación push con título, cuerpo, datos adicionales y metadatos.
 */
public record PushNotification(
        NotificationId id,
        Instant createdAt,
        DeviceToken recipient,
        String title,
        String body,
        Map<String, String> data,
        Map<String, String> metadata
) implements Notification {

    public PushNotification {
        data = data == null ? Map.of() : Map.copyOf(data);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}