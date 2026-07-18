package org.challenge.config;

import org.apache.commons.lang3.StringUtils;
import org.challenge.model.email.EmailAddress;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Configuración para el proveedor de email SendGrid.
 */
public final class SendGridConfig {

    private static final URI DEFAULT_ENDPOINT =
            URI.create("https://api.sendgrid.com/v3");

    private final SecretValue apiKey;
    private final EmailAddress fromAddress;
    private final String fromName;
    private final URI endpoint;

    private SendGridConfig(Builder builder) {
        this.apiKey = Objects.requireNonNull(
                builder.apiKey,
                "SendGrid API key is required"
        );

        this.fromAddress = Objects.requireNonNull(
                builder.fromAddress,
                "SendGrid from address is required"
        );

        this.fromName = normalizeOptional(builder.fromName);

        this.endpoint = builder.endpoint == null
                ? DEFAULT_ENDPOINT
                : builder.endpoint;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SecretValue apiKey() {
        return apiKey;
    }

    public EmailAddress fromAddress() {
        return fromAddress;
    }

    public Optional<String> fromName() {
        return Optional.ofNullable(fromName);
    }

    public URI endpoint() {
        return endpoint;
    }

    private static String normalizeOptional(String value) {
        return StringUtils.isBlank(value)
                ? null
                : value.trim();
    }

    @Override
    public String toString() {
        return "SendGridConfig{" +
                "apiKey=********" +
                ", fromAddress=" + fromAddress +
                ", fromName='" + fromName + '\'' +
                ", endpoint=" + endpoint +
                '}';
    }

    public static final class Builder {

        private SecretValue apiKey;
        private EmailAddress fromAddress;
        private String fromName;
        private URI endpoint;

        private Builder() {
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = SecretValue.of(apiKey);
            return this;
        }

        public Builder apiKey(SecretValue apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder fromAddress(String fromAddress) {
            this.fromAddress = EmailAddress.of(fromAddress);
            return this;
        }

        public Builder fromAddress(EmailAddress fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        public Builder fromName(String fromName) {
            this.fromName = fromName;
            return this;
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        /**
         * Permite configurar el builder mediante un bloque de código lambda.
         *
         * <p>Esto facilita la construcción en cadena cuando se desea agrupar
         * varias llamadas de configuración dentro de un bloque.</p>
         *
         * @param configurer consumidor que recibe este builder para configurarlo
         * @return este builder para continuar la construcción en cadena
         */
        public Builder configure(Consumer<Builder> configurer) {
            configurer.accept(this);
            return this;
        }

        public SendGridConfig build() {
            return new SendGridConfig(this);
        }
    }
}