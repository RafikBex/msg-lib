package org.challenge.validation;

import org.apache.commons.lang3.StringUtils;
import org.challenge.exception.NotificationValidationException;
import org.challenge.model.email.EmailNotification;

import java.time.Instant;

/**
 * Valida la estructura y los campos requeridos de una notificación por email.
 *
 * <p>La validación del formato del email pertenece al Value Object
 * {@link org.challenge.model.email.EmailAddress}.</p>
 */
public final class EmailNotificationValidator implements NotificationValidator<EmailNotification> {

    private static final int DEFAULT_MAX_SUBJECT_LENGTH = 255;
    private static final int DEFAULT_MAX_BODY_LENGTH = 10_000;
    private static final int DEFAULT_MAX_METADATA_ENTRIES = 100;

    private final int maxSubjectLength;
    private final int maxBodyLength;
    private final int maxMetadataEntries;

    public EmailNotificationValidator() {
        this(DEFAULT_MAX_SUBJECT_LENGTH, DEFAULT_MAX_BODY_LENGTH, DEFAULT_MAX_METADATA_ENTRIES);
    }

    public EmailNotificationValidator(int maxSubjectLength, int maxBodyLength, int maxMetadataEntries) {
        if (maxSubjectLength <= 0) {
            throw new IllegalArgumentException("Maximum email subject length must be greater than zero");
        }

        if (maxBodyLength <= 0) {
            throw new IllegalArgumentException("Maximum email body length must be greater than zero");
        }

        if (maxMetadataEntries < 0) {
            throw new IllegalArgumentException("Maximum email metadata entries cannot be negative");
        }

        this.maxSubjectLength = maxSubjectLength;
        this.maxBodyLength = maxBodyLength;
        this.maxMetadataEntries = maxMetadataEntries;
    }

    @Override
    public void validate(EmailNotification notification) {
        if (notification == null) {
            throw new NotificationValidationException("Email notification cannot be null");
        }

        validateId(notification);
        validateCreatedAt(notification);
        validateRecipient(notification);
        validateSubject(notification);
        validateBody(notification);
        validateMetadata(notification);
    }

    private void validateId(EmailNotification notification) {
        if (notification.id() == null) {
            throw NotificationValidationException.forField(null, "id", "Notification ID is required");
        }
    }

    private void validateCreatedAt(EmailNotification notification) {
        if (notification.createdAt() == null) {
            throw NotificationValidationException.forField(notification.id(), "createdAt", "Creation date is required");
        }

        if (notification.createdAt().isAfter(Instant.now())) {
            throw NotificationValidationException.forField(notification.id(), "createdAt", "Creation date cannot be in the future");
        }
    }

    private void validateRecipient(EmailNotification notification) {
        if (notification.recipient() == null) {
            throw NotificationValidationException.forField(notification.id(), "recipient", "Email recipient is required");
        }
    }

    private void validateSubject(EmailNotification notification) {
        String subject = notification.subject();

        if (StringUtils.isBlank(subject)) {
            throw NotificationValidationException.forField(notification.id(), "subject", "Email subject is required");
        }

        if (subject.length() > maxSubjectLength) {
            throw NotificationValidationException.forField(notification.id(), "subject", "Email subject cannot exceed %d characters".formatted(maxSubjectLength));
        }
    }

    private void validateBody(EmailNotification notification) {
        String body = notification.body();

        if (StringUtils.isBlank(body)) {
            throw NotificationValidationException.forField(notification.id(), "body", "Email body is required");
        }

        if (body.length() > maxBodyLength) {
            throw NotificationValidationException.forField(notification.id(), "body", "Email body cannot exceed %d characters".formatted(maxBodyLength));
        }
    }

    private void validateMetadata(EmailNotification notification) {
        if (notification.metadata() == null) {
            throw NotificationValidationException.forField(notification.id(), "metadata", "Email metadata cannot be null");
        }

        if (notification.metadata().size() > maxMetadataEntries) {
            throw NotificationValidationException.forField(notification.id(), "metadata", "Email metadata cannot contain more than %d entries".formatted(maxMetadataEntries));
        }

        notification.metadata().forEach((key, value) -> {
            if (StringUtils.isBlank(key)) {
                throw NotificationValidationException.forField(notification.id(), "metadata", "Email metadata cannot contain blank keys");
            }

            if (value == null) {
                throw NotificationValidationException.forField(notification.id(), "metadata." + key, "Email metadata values cannot be null");
            }
        });
    }
}
