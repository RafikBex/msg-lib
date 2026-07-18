package org.challenge.core;

import org.challenge.notification.api.Notification;
import org.challenge.notification.api.SendResult;

/**
 * Pipeline que ejecuta el flujo completo de envío para un tipo de notificación.
 * <p>
 * Normalmente incluye validación, selección de proveedor, envío y publicación de eventos.
 */
public interface ChannelPipeline<N extends Notification> {

    /**
     * Ejecuta el pipeline para la notificación recibida.
     *
     * @param notification la notificación a enviar
     * @return el resultado del envío
     */
    SendResult execute(N notification);
}