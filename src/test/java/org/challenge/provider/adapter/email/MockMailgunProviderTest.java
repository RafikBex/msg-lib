package org.challenge.provider.adapter.email;

import org.challenge.config.MailgunConfig;
import org.challenge.model.email.EmailAddress;
import org.challenge.model.email.EmailNotification;
import org.challenge.notification.api.NotificationId;
import org.challenge.notification.api.SendResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifica la construcción y el comportamiento básico del
 * proveedor simulado de Mailgun.
 */
class MockMailgunProviderTest {

    @Test
    void shouldCreateProviderWithConfig() {
        MailgunConfig config = MailgunConfig.builder()
                .apiKey("test-key")
                .domain("example.com")
                .fromAddress("sender@example.com")
                .build();

        MockMailgunProvider provider = new MockMailgunProvider(config);

        assertNotNull(provider);
        assertEquals("mailgun", provider.providerName());
    }

    @Test
    void shouldSendEmailAndReturnSentResult() {
        MailgunConfig config = MailgunConfig.builder()
                .apiKey("test-key")
                .domain("example.com")
                .fromAddress("sender@example.com")
                .build();

        MockMailgunProvider provider = new MockMailgunProvider(config);

        EmailNotification notification = new EmailNotification(
                NotificationId.generate(),
                Instant.now(),
                EmailAddress.of("recipient@example.com"),
                "Test subject",
                "Test body",
                Map.of()
        );

        SendResult result = provider.send(notification);

        assertNotNull(result);
        assertEquals(notification.id(), result.notificationId());
        assertEquals("mailgun", result.provider());
        assertNotNull(result.providerMessageId());
    }
}
