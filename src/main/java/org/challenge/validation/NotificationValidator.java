package org.challenge.validation;

import org.challenge.notification.api.Notification;

/**
 * Contrato para validar una notificación antes de que entre en el pipeline de envío.
 */
public interface NotificationValidator<N extends Notification> {

    /**
     * Valida la notificación y lanza {@link org.challenge.exception.NotificationValidationException} si es inválida.
     */
    void validate(N notification);
}