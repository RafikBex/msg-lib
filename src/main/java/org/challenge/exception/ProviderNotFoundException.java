package org.challenge.exception;

import java.io.Serial;
import java.util.Objects;

/**
 * Indica que no existe un proveedor registrado con el
 * nombre solicitado para un tipo de notificación.
 */
public final class ProviderNotFoundException
        extends NotificationException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String providerName;
    private final Class<?> notificationType;

    public ProviderNotFoundException(String providerName) {
        this(providerName, null);
    }

    public ProviderNotFoundException(
            String providerName,
            Class<?> notificationType
    ) {
        super(
                NotificationErrorCode.PROVIDER_NOT_FOUND,
                buildMessage(providerName, notificationType)
        );

        this.providerName = requireProviderName(providerName);
        this.notificationType = notificationType;
    }

    /**
     * Nombre del proveedor que no fue encontrado.
     */
    public String providerName() {
        return providerName;
    }

    /**
     * Tipo de notificación para el que se solicitó el proveedor.
     */
    public Class<?> notificationType() {
        return notificationType;
    }

    private static String buildMessage(
            String providerName,
            Class<?> notificationType
    ) {
        String validProviderName =
                requireProviderName(providerName);

        if (notificationType == null) {
            return "Provider '%s' is not registered"
                    .formatted(validProviderName);
        }

        return "Provider '%s' is not registered for notification type '%s'"
                .formatted(
                        validProviderName,
                        notificationType.getSimpleName()
                );
    }

    private static String requireProviderName(
            String providerName
    ) {
        Objects.requireNonNull(
                providerName,
                "Provider name cannot be null"
        );

        if (providerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Provider name cannot be blank"
            );
        }

        return providerName.trim();
    }
}