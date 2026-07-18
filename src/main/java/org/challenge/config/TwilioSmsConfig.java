package org.challenge.config;

import org.apache.commons.lang3.StringUtils;
import org.challenge.model.sms.PhoneNumber;

import java.net.URI;
import java.util.Objects;

/**
 * Configuración para el proveedor de SMS Twilio.
 */
public final class TwilioSmsConfig {

    private static final URI DEFAULT_ENDPOINT =
            URI.create("https://api.twilio.com/");

    private final String accountSid;
    private final SecretValue authToken;
    private final PhoneNumber fromNumber;
    private final URI endpoint;

    private TwilioSmsConfig(Builder builder) {
        this.accountSid = validateAccountSid(builder.accountSid);

        this.authToken = Objects.requireNonNull(
                builder.authToken,
                "Twilio auth token is required"
        );

        this.fromNumber = Objects.requireNonNull(
                builder.fromNumber,
                "Twilio from number is required"
        );

        this.endpoint = builder.endpoint == null
                ? DEFAULT_ENDPOINT
                : builder.endpoint;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String accountSid() {
        return accountSid;
    }

    public SecretValue authToken() {
        return authToken;
    }

    public PhoneNumber fromNumber() {
        return fromNumber;
    }

    public URI endpoint() {
        return endpoint;
    }

    private static String validateAccountSid(String accountSid) {
        if (StringUtils.isBlank(accountSid)) {
            throw new IllegalArgumentException(
                    "Twilio account SID cannot be null or blank"
            );
        }

        String normalized = accountSid.trim();

        /*
         * Los Account SID de Twilio normalmente comienzan con AC.
         * La validación se mantiene flexible para permitir simulaciones.
         */
        if (!normalized.startsWith("AC")) {
            throw new IllegalArgumentException(
                    "Twilio account SID must start with 'AC'"
            );
        }

        return normalized;
    }

    @Override
    public String toString() {
        return "TwilioSmsConfig{" +
                "accountSid='" + accountSid + '\'' +
                ", authToken=********" +
                ", fromNumber=" + fromNumber +
                ", endpoint=" + endpoint +
                '}';
    }

    public static final class Builder {

        private String accountSid;
        private SecretValue authToken;
        private PhoneNumber fromNumber;
        private URI endpoint;

        private Builder() {
        }

        public Builder accountSid(String accountSid) {
            this.accountSid = accountSid;
            return this;
        }

        public Builder authToken(String authToken) {
            this.authToken = SecretValue.of(authToken);
            return this;
        }

        public Builder authToken(SecretValue authToken) {
            this.authToken = authToken;
            return this;
        }

        public Builder fromNumber(String fromNumber) {
            this.fromNumber = PhoneNumber.of(fromNumber);
            return this;
        }

        public Builder fromNumber(PhoneNumber fromNumber) {
            this.fromNumber = fromNumber;
            return this;
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public TwilioSmsConfig build() {
            return new TwilioSmsConfig(this);
        }
    }
}