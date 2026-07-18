package org.challenge.event;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Publicador de eventos en memoria basado en una lista de suscriptores.
 */
public final class InMemoryNotificationEventPublisher implements NotificationEventPublisher {

    private static final System.Logger LOGGER = System.getLogger(InMemoryNotificationEventPublisher.class.getName());

    private final CopyOnWriteArrayList<NotificationEventListener> listeners;

    public InMemoryNotificationEventPublisher() {
        this.listeners = new CopyOnWriteArrayList<>();
    }

    public InMemoryNotificationEventPublisher(Collection<? extends NotificationEventListener> listeners) {
        Objects.requireNonNull(listeners, "Listeners collection cannot be null");
        this.listeners = new CopyOnWriteArrayList<>();
        listeners.forEach(this::subscribe);
    }

    /**
     * Registra un nuevo listener si no está ya presente.
     */
    public void subscribe(NotificationEventListener listener) {
        Objects.requireNonNull(listener, "Notification event listener cannot be null");
        listeners.addIfAbsent(listener);
    }

    /**
     * Elimina un listener previamente registrado.
     */
    public boolean unsubscribe(NotificationEventListener listener) {
        Objects.requireNonNull(listener, "Notification event listener cannot be null");
        return listeners.remove(listener);
    }

    /**
     * Devuelve una copia de los listeners registrados.
     */
    public List<NotificationEventListener> listeners() {
        return List.copyOf(listeners);
    }

    @Override
    public void publish(NotificationEvent event) {
        Objects.requireNonNull(event, "Notification event cannot be null");
        for (NotificationEventListener listener : listeners) {
            notifyListener(listener, event);
        }
    }

    private void notifyListener(NotificationEventListener listener, NotificationEvent event) {
        try {
            listener.onEvent(event);
        } catch (RuntimeException exception) {
            LOGGER.log(System.Logger.Level.ERROR, "Notification event listener {0} failed while " + "processing event for notification {1}", listener.getClass().getName(), event.notificationId());

            LOGGER.log(System.Logger.Level.DEBUG, "Listener failure details", exception);
        }
    }
}