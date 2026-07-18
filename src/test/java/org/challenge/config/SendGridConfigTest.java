package org.challenge.config;

import org.challenge.model.email.EmailAddress;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la construcción de {@link SendGridConfig} mediante su builder.
 */
class SendGridConfigTest {

    @Test
    void shouldBuildSendGridConfigWithMandatoryFields() {
        SendGridConfig config = SendGridConfig.builder()
                .apiKey("test-api-key")
                .fromAddress("sender@example.com")
                .build();

        assertNotNull(config);
        assertNotNull(config.apiKey());
        assertEquals(EmailAddress.of("sender@example.com"), config.fromAddress());
    }

    @Test
    void shouldBuildSendGridConfigWithAllFields() {
        URI customEndpoint = URI.create("https://custom.sendgrid.example.com/v3");

        SendGridConfig config = SendGridConfig.builder()
                .apiKey(SecretValue.of("secret"))
                .fromAddress(EmailAddress.of("noreply@example.com"))
                .fromName("Test Service")
                .endpoint(customEndpoint)
                .build();

        assertNotNull(config);
        assertEquals("secret", config.apiKey().reveal());
        assertEquals(EmailAddress.of("noreply@example.com"), config.fromAddress());
        assertTrue(config.fromName().isPresent());
        assertEquals("Test Service", config.fromName().get());
        assertEquals(customEndpoint, config.endpoint());
    }
}
