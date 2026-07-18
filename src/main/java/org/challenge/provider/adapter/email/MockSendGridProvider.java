package org.challenge.provider.adapter.email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.challenge.config.SendGridConfig;
import org.challenge.exception.NotificationDeliveryException;
import org.challenge.model.email.EmailNotification;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.NotificationProvider;

import java.util.Objects;
import java.util.UUID;

/**
 * Proveedor simulado de SendGrid para envío de emails.
 * <p>
 * No realiza llamadas HTTP; solo registra y devuelve un resultado de éxito.
 */
public final class MockSendGridProvider implements NotificationProvider<EmailNotification> {

    private static final Logger logger = LogManager.getLogger(MockSendGridProvider.class);

    private final SendGridConfig config;

    public MockSendGridProvider(SendGridConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String providerName() {
        return "sendgrid";
    }

    @Override
    public SendResult send(EmailNotification notification) {
        try {
            logger.info(
                    "Simulating SendGrid email to {}",
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
                    "Could not send email",
                    exception
            );
        }
    }
}