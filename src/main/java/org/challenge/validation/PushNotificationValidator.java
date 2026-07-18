package org.challenge.validation;

import org.apache.commons.lang3.StringUtils;
import org.challenge.exception.NotificationValidationException;
import org.challenge.model.push.PushNotification;

import java.time.Instant;

/**
 * Valida la estructura y los campos requeridos de una
 * notificación Push.
 * <p>
 * La validación del formato del token pertenece
 * al Value Object DeviceToken.
 */
public final class PushNotificationValidator
        implements NotificationValidator<PushNotification> {

    private static final int DEFAULT_MAX_TITLE_LENGTH = 200;
    private static final int DEFAULT_MAX_BODY_LENGTH = 4_096;
    private static final int DEFAULT_MAX_DATA_ENTRIES = 100;

    private final int maxTitleLength;
    private final int maxBodyLength;
    private final int maxDataEntries;

    public PushNotificationValidator() {
        this(
                DEFAULT_MAX_TITLE_LENGTH,
                DEFAULT_MAX_BODY_LENGTH,
                DEFAULT_MAX_DATA_ENTRIES
        );
    }

    public PushNotificationValidator(
            int maxTitleLength,
            int maxBodyLength,
            int maxDataEntries
    ) {
        if (maxTitleLength <= 0) {
            throw new IllegalArgumentException(
                    "Maximum push title length must be greater than zero"
            );
        }

        if (maxBodyLength <= 0) {
            throw new IllegalArgumentException(
                    "Maximum push body length must be greater than zero"
            );
        }

        if (maxDataEntries < 0) {
            throw new IllegalArgumentException(
                    "Maximum push data entries cannot be negative"
            );
        }

        this.maxTitleLength = maxTitleLength;
        this.maxBodyLength = maxBodyLength;
        this.maxDataEntries = maxDataEntries;
    }

    @Override
    public void validate(PushNotification notification) {
        if (notification == null) {
            throw new NotificationValidationException(
                    "Push notification cannot be null"
            );
        }

        validateId(notification);
        validateCreatedAt(notification);
        validateRecipient(notification);
        validateTitle(notification);
        validateBody(notification);
        validateData(notification);
    }

    private void validateId(PushNotification notification) {
        if (notification.id() == null) {
            throw NotificationValidationException.forField(
                    null,
                    "id",
                    "Notification ID is required"
            );
        }
    }

    private void validateCreatedAt(PushNotification notification) {
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

    private void validateRecipient(PushNotification notification) {
        if (notification.recipient() == null) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "recipient",
                    "Device token is required"
            );
        }
    }

    private void validateTitle(PushNotification notification) {
        String title = notification.title();

        if (StringUtils.isBlank(title)) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "title",
                    "Push notification title cannot be null or blank"
            );
        }

        if (title.length() > maxTitleLength) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "title",
                    "Push notification title cannot exceed %d characters"
                            .formatted(maxTitleLength)
            );
        }
    }

    private void validateBody(PushNotification notification) {
        String body = notification.body();

        if (StringUtils.isBlank(body)) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "body",
                    "Push notification body cannot be null or blank"
            );
        }

        if (body.length() > maxBodyLength) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "body",
                    "Push notification body cannot exceed %d characters"
                            .formatted(maxBodyLength)
            );
        }
    }

    private void validateData(PushNotification notification) {
        if (notification.data() == null) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "data",
                    "Push notification data cannot be null"
            );
        }

        if (notification.data().size() > maxDataEntries) {
            throw NotificationValidationException.forField(
                    notification.id(),
                    "data",
                    "Push notification data cannot contain more than %d entries"
                            .formatted(maxDataEntries)
            );
        }

        notification.data().forEach((key, value) -> {
            if (StringUtils.isBlank(key)) {
                throw NotificationValidationException.forField(
                        notification.id(),
                        "data",
                        "Push notification data cannot contain blank keys"
                );
            }

            if (value == null) {
                throw NotificationValidationException.forField(
                        notification.id(),
                        "data." + key,
                        "Push notification data values cannot be null"
                );
            }
        });
    }
}