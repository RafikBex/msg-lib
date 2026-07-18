package org.challenge.provider.adapter.push;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.challenge.config.FirebaseConfig;
import org.challenge.exception.NotificationDeliveryException;
import org.challenge.model.push.PushNotification;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.NotificationProvider;

import java.util.Objects;
import java.util.UUID;

/**
 * Proveedor simulado de Firebase Cloud Messaging para envío de notificaciones push.
 * <p>
 * No realiza llamadas HTTP; solo registra y devuelve un resultado de éxito.
 */
public final class MockFirebaseProvider implements NotificationProvider<PushNotification> {

    private static final Logger logger = LogManager.getLogger(MockFirebaseProvider.class);

    private final FirebaseConfig config;

    public MockFirebaseProvider(FirebaseConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String providerName() {
        return "firebase";
    }

    @Override
    public SendResult send(PushNotification notification) {
        try {
            logger.info(
                    "Simulating Firebase push notification to {}",
                    notification.recipient().value()
            );

            return SendResult.sent(
                    notification.id(),
                    providerName(),
                    UUID.randomUUID().toString()
            );
        } catch (RuntimeException exception) {
            throw new NotificationDeliveryException(
                    providerName(),
                    notification.id(),
                    "Could not send push notification",
                    exception
            );
        }
    }
}
