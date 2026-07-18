package org.challenge.provider;

import org.challenge.notification.api.Notification;
import org.challenge.notification.api.SendResult;

/**
 * Adaptador que envía una notificación a través de un proveedor concreto.
 */
public interface NotificationProvider<N extends Notification> {

    /**
     * Nombre identificador del proveedor.
     */
    String providerName();

    /**
     * Envía la notificación y devuelve el resultado.
     */
    SendResult send(N notification);
}