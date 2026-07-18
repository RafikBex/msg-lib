package org.challenge.provider;

import org.challenge.notification.api.Notification;

/**
 * Estrategia para seleccionar el proveedor adecuado para una notificación.
 */
public interface ProviderRoutingStrategy<N extends Notification> {

    /**
     * Selecciona el proveedor que procesará la notificación.
     */
    NotificationProvider<N> selectProvider(N notification);
}