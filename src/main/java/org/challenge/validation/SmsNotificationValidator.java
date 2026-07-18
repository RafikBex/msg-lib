package org.challenge.validation;


import org.apache.commons.lang3.StringUtils;
import org.challenge.exception.NotificationValidationException;
import org.challenge.model.sms.SmsNotification;

import java.time.Instant;

/**
 * Valida la estructura y los datos requeridos de una notificación SMS.
 *
 * La validación del formato del número telefónico pertenece
 * al Value Object PhoneNumber.
 */
public final class SmsNotificationValidator
        implements NotificationValidator<SmsNotification> {

    /*
     * Límite interno configurable de la librería.
     * Un proveedor concreto puede aplicar restricciones adicionales.
     */
    private static final int DEFAULT_MAX_BODY_LENGTH = 1_600;

    private final int maxBodyLength;

    public SmsNotificationValidator() {
        this(DEFAULT_MAX_BODY_LENGTH);
    }

    public SmsNotificationValidator(int maxBodyLength) {
        if (maxBodyLength <= 0) {
            throw new IllegalArgumentException(
                    "Maximum SMS body length must be greater than zero"
            );
        }

        this.maxBodyLength = maxBodyLength;
    }

    @Override
    public void validate(SmsNotification notification) {
        if (notification == null) {
            throw new NotificationValidationException(
                    "SMS notification cannot be null"
            );
        }

        validateId(notification);
        validateCreatedAt(notification);
        validateRecipient(notification);
        validateBody(notification);
    }

    private void validateId(SmsNotification notification) {
        if (notification.id() == null) {
            throw NotificationValidationException.forField(
                    null,
                    "id",
                    "Notification ID is required"
            );
        }
    }

    private void validateCreatedAt(SmsNotification notification) {
        if (notification.createdAt() == null) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "createdAt",
                    "Creation date is required"
            );
        }

        if (notification.createdAt().isAfter(Instant.now())) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "createdAt",
                    "Creation date cannot be in the future"
            );
        }
    }

    private void validateRecipient(SmsNotification notification) {
        if (notification.recipient() == null) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "recipient",
                    "Phone number is required"
            );
        }
    }

    private void validateBody(SmsNotification notification) {
        String body = notification.body();

        if (StringUtils.isBlank(body)) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "body",
                    "SMS body cannot be null or blank"
            );
        }

        if (body.length() > maxBodyLength) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "body",
                    "SMS body cannot exceed %d characters"
                            .formatted(maxBodyLength)
            );
        }
    }
}