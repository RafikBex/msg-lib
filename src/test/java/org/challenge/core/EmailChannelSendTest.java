package org.challenge.core;

import org.challenge.config.SendGridConfig;
import org.challenge.event.NoOpNotificationEventPublisher;
import org.challenge.model.email.EmailAddress;
import org.challenge.model.email.EmailNotification;
import org.challenge.notification.api.NotificationClient;
import org.challenge.notification.api.NotificationId;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.FixedProviderRouting;
import org.challenge.provider.adapter.email.MockSendGridProvider;
import org.challenge.validation.EmailNotificationValidator;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifica el envío completo de una notificación por el canal de email,
 * desde el cliente hasta el proveedor simulado.
 */
class EmailChannelSendTest {

    @Test
    void shouldSendEmailNotificationThroughChannelPipeline() {
        SendGridConfig config = SendGridConfig.builder()
                .apiKey("test-key")
                .fromAddress("sender@example.com")
                .build();

        MockSendGridProvider provider = new MockSendGridProvider(config);

        ChannelPipeline<EmailNotification> pipeline = new DefaultChannelPipeline<>(
                new EmailNotificationValidator(),
                new FixedProviderRouting<>(provider),
                NoOpNotificationEventPublisher.INSTANCE
        );

        HandlerRegistry registry = new HandlerRegistry(
                Map.of(EmailNotification.class, pipeline)
        );

        try (var executor = Executors.newSingleThreadExecutor()) {
            NotificationClient client = new DefaultNotificationClient(registry, executor);

            EmailNotification email = new EmailNotification(
                    NotificationId.generate(),
                    Instant.now(),
                    EmailAddress.of("recipient@example.com"),
                    "Test subject",
                    "Test body",
                    Map.of()
            );

            SendResult result = client.send(email);

            assertNotNull(result);
            assertEquals("sendgrid", result.provider());
            assertEquals(email.id(), result.notificationId());
            assertNotNull(result.providerMessageId());
        }
    }
}
