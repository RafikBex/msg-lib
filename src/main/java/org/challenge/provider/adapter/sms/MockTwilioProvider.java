package org.challenge.provider.adapter.sms;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.challenge.config.TwilioSmsConfig;
import org.challenge.exception.NotificationDeliveryException;
import org.challenge.model.sms.SmsNotification;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.NotificationProvider;
import org.challenge.provider.adapter.SimulationDelays;

import java.util.Objects;
import java.util.UUID;

/**
 * Proveedor simulado de Twilio para envío de SMS.
 * <p>
 * No realiza llamadas HTTP; solo registra y devuelve un resultado de éxito.
 */
public final class MockTwilioProvider implements NotificationProvider<SmsNotification> {

    private static final Logger logger = LogManager.getLogger(MockTwilioProvider.class);

    private final TwilioSmsConfig config;

    public MockTwilioProvider(TwilioSmsConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public String providerName() {
        return "twilio";
    }

    @Override
    public SendResult send(SmsNotification notification) {
        try {
            SimulationDelays.simulateNetworkLatency(logger);

            logger.info(
                    "Simulating Twilio SMS to {}",
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
                    "Could not send SMS",
                    exception
            );
        }
    }
}
