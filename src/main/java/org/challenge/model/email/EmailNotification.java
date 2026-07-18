package org.challenge.model.email;



import org.challenge.notification.api.Notification;
import org.challenge.notification.api.NotificationId;

import java.time.Instant;
import java.util.Map;

/**
 * Notificación por email con destinatario, asunto y cuerpo del mensaje.
 */
public record EmailNotification(
        NotificationId id,
        Instant createdAt,
        EmailAddress recipient,
        String subject,
        String body,
        Map<String, String> metadata
) implements Notification {

    public EmailNotification {
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}