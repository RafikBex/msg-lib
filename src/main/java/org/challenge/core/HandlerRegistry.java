package org.challenge.core;

import org.challenge.exception.UnsupportedNotificationException;
import org.challenge.notification.api.Notification;

import java.util.Map;

/**
 * Registro que asocia cada tipo de notificación con su pipeline de procesamiento.
 */
public final class HandlerRegistry {

    private final Map<Class<? extends Notification>, ChannelPipeline<? extends Notification>> pipelines;

    public HandlerRegistry(Map<Class<? extends Notification>, ChannelPipeline<? extends Notification>> pipelines) {
        this.pipelines = Map.copyOf(pipelines);
    }

    /**
     * Devuelve el pipeline registrado para el tipo de notificación indicado.
     *
     * @throws UnsupportedNotificationException si no hay un pipeline registrado
     */
    @SuppressWarnings("unchecked")
    public <N extends Notification> ChannelPipeline<N> findFor(Class<N> notificationType) {
        ChannelPipeline<?> pipeline = pipelines.get(notificationType);

        if (pipeline == null) {
            throw new UnsupportedNotificationException(notificationType);
        }

        return (ChannelPipeline<N>) pipeline;
    }
}