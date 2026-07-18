package org.challenge.event;

/**
 * Publicador de eventos que no realiza ninguna acción.
 */
public enum NoOpNotificationEventPublisher implements NotificationEventPublisher {

    INSTANCE;

    @Override
    public void publish(NotificationEvent event) {
        // Sin operación.
    }
}
