package org.challenge.event;

/**
 * Publicador de eventos del ciclo de vida de las notificaciones.
 */
public interface NotificationEventPublisher {

    /**
     * Publica un evento para los suscriptores registrados.
     */
    void publish(NotificationEvent event);

    /**
     * Devuelve un publicador que ignora todos los eventos.
     */
    static NotificationEventPublisher noOp() {
        return NoOpNotificationEventPublisher.INSTANCE;
    }
}