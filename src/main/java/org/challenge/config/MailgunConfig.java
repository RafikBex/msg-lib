package org.challenge.config;

import org.apache.commons.lang3.StringUtils;
import org.challenge.model.email.EmailAddress;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Configuración para el proveedor de email Mailgun.
 */
public final class MailgunConfig {

    private static final URI DEFAULT_ENDPOINT = URI.create("https://api.mailgun.net/v3");

    private static final Pattern DOMAIN_PATTERN = Pattern.compile("^(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+" + "[a-zA-Z]{2,}$");

    private final SecretValue apiKey;
    private final String domain;
    private final EmailAddress fromAddress;
    private final String fromName;
    private final URI endpoint;

    private MailgunConfig(Builder builder) {
        this.apiKey = Objects.requireNonNull(builder.apiKey, "Mailgun API key is required");

        this.domain = validateDomain(builder.domain);

        this.fromAddress = Objects.requireNonNull(builder.fromAddress, "Mailgun from address is required");

        this.fromName = StringUtils.isBlank(builder.fromName) ? null : builder.fromName.trim();

        this.endpoint = builder.endpoint == null ? DEFAULT_ENDPOINT : builder.endpoint;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SecretValue apiKey() {
        return apiKey;
    }

    public String domain() {
        return domain;
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

    private static String validateDomain(String domain) {
        if (StringUtils.isBlank(domain)) {
            throw new IllegalArgumentException("Mailgun domain cannot be null or blank");
        }

        String normalizedDomain = domain.trim().toLowerCase();

        if (!DOMAIN_PATTERN.matcher(normalizedDomain).matches()) {
            throw new IllegalArgumentException("Invalid Mailgun domain: " + normalizedDomain);
        }

        return normalizedDomain;
    }

    @Override
    public String toString() {
        return "MailgunConfig{" + "apiKey=********" + ", domain='" + domain + '\'' + ", fromAddress=" + fromAddress + ", fromName='" + fromName + '\'' + ", endpoint=" + endpoint + '}';
    }

    public static final class Builder {

        private SecretValue apiKey;
        private String domain;
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

        public Builder domain(String domain) {
            this.domain = domain;
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

        public MailgunConfig build() {
            return new MailgunConfig(this);
        }
    }
}