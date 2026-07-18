package org.challenge.model.sms;

import org.challenge.notification.api.Notification;
import org.challenge.notification.api.NotificationId;

import java.time.Instant;
import java.util.Map;

/**
 * Notificación SMS con destinatario y cuerpo del mensaje.
 */
public record SmsNotification(
        NotificationId id,
        Instant createdAt,
        PhoneNumber recipient,
        String body,
        Map<String, String> metadata
) implements Notification {

    public SmsNotification {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}