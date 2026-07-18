package org.challenge.config;

import org.challenge.model.email.EmailAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifica la construcción de {@link SendGridConfig} usando un builder
 * encadenado con un bloque de código (lambda) como parámetro.
 */
class SendGridConfigBuilderLambdaTest {

    @Test
    void shouldBuildConfigUsingLambdaConfigurer() {
        SendGridConfig config = SendGridConfig.builder()
                .configure(builder -> {
                    builder.apiKey("lambda-api-key");
                    builder.fromAddress("lambda@example.com");
                    builder.fromName("Lambda Builder");
                })
                .build();

        assertNotNull(config);
        assertEquals("lambda-api-key", config.apiKey().reveal());
        assertEquals(EmailAddress.of("lambda@example.com"), config.fromAddress());
        assertTrue(config.fromName().isPresent());
        assertEquals("Lambda Builder", config.fromName().get());
    }

    @Test
    void shouldCombineLambdaConfigurerWithChainedCalls() {
        SendGridConfig config = SendGridConfig.builder()
                .configure(builder -> builder
                        .apiKey("combined-api-key")
                        .fromAddress("combined@example.com")
                )
                .fromName("Combined Builder")
                .build();

        assertNotNull(config);
        assertEquals("combined-api-key", config.apiKey().reveal());
        assertEquals(EmailAddress.of("combined@example.com"), config.fromAddress());
        assertTrue(config.fromName().isPresent());
        assertEquals("Combined Builder", config.fromName().get());
    }
}
