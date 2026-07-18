package org.challenge.config;

import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Objects;

/**
 * Configuración para el proveedor de notificaciones push Firebase Cloud Messaging.
 */
public final class FirebaseConfig {

    private static final URI DEFAULT_ENDPOINT = URI.create("https://fcm.googleapis.com/v1");

    private final String projectId;
    private final SecretValue serviceAccountJson;
    private final URI endpoint;
    private final boolean validateOnly;

    private FirebaseConfig(Builder builder) {
        this.projectId = validateProjectId(builder.projectId);

        this.serviceAccountJson = Objects.requireNonNull(builder.serviceAccountJson, "Firebase service account JSON is required");

        this.endpoint = builder.endpoint == null ? DEFAULT_ENDPOINT : builder.endpoint;

        this.validateOnly = builder.validateOnly;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String projectId() {
        return projectId;
    }

    public SecretValue serviceAccountJson() {
        return serviceAccountJson;
    }

    public URI endpoint() {
        return endpoint;
    }

    public boolean validateOnly() {
        return validateOnly;
    }

    public URI messagesEndpoint() {
        return endpoint.resolve("/v1/projects/%s/messages:send".formatted(projectId));
    }

    private static String validateProjectId(String projectId) {
        if (StringUtils.isBlank(projectId)) {
            throw new IllegalArgumentException("Firebase project ID cannot be null or blank");
        }

        return projectId.trim();
    }

    @Override
    public String toString() {
        return "FirebaseConfig{" + "projectId='" + projectId + '\'' + ", serviceAccountJson=********" + ", endpoint=" + endpoint + ", validateOnly=" + validateOnly + '}';
    }

    public static final class Builder {

        private String projectId;
        private SecretValue serviceAccountJson;
        private URI endpoint;
        private boolean validateOnly;

        private Builder() {
        }

        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }

        public Builder serviceAccountJson(String serviceAccountJson) {
            this.serviceAccountJson = SecretValue.of(serviceAccountJson);

            return this;
        }

        public Builder serviceAccountJson(SecretValue serviceAccountJson) {
            this.serviceAccountJson = serviceAccountJson;
            return this;
        }

        public Builder endpoint(URI endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder validateOnly(boolean validateOnly) {
            this.validateOnly = validateOnly;
            return this;
        }

        public FirebaseConfig build() {
            return new FirebaseConfig(this);
        }
    }
}