package org.challenge.provider.adapter.email;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.challenge.config.MailgunConfig;
import org.challenge.exception.NotificationDeliveryException;
import org.challenge.model.email.EmailNotification;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.NotificationProvider;
import org.challenge.provider.adapter.SimulationDelays;

import java.util.Objects;
import java.util.UUID;

/**
 * Adaptador simulado para el proveedor de email Mailgun.
 *
 * <p>No realiza llamadas de red; solo imprime un mensaje de simulación
 * y devuelve un {@link SendResult} con estado {@code SENT}.</p>
 */
public final class MockMailgunProvider implements NotificationProvider<EmailNotification> {

    private static final Logger logger = LogManager.getLogger(MockMailgunProvider.class);

    private final MailgunConfig config;

    public MockMailgunProvider(MailgunConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String providerName() {
        return "mailgun";
    }

    @Override
    public SendResult send(EmailNotification notification) {
        try {
            SimulationDelays.simulateNetworkLatency(logger);

            logger.info(
                    "Simulating Mailgun email to {}",
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
