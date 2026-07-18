package org.challenge.event;

/**
 * Suscriptor de eventos del ciclo de vida de las notificaciones.
 */
@FunctionalInterface
public interface NotificationEventListener {

    /**
     * Procesa un evento publicado.
     */
    void onEvent(NotificationEvent event);
}
