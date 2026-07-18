package org.challenge.notification.api;


import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Cliente de alto nivel para enviar notificaciones a través de los canales configurados.
 */
public interface NotificationClient {

    /**
     * Envía una notificación de forma síncrona.
     */
    <N extends Notification> SendResult send(N notification);

    /**
     * Envía una notificación de forma asíncrona.
     */
    <N extends Notification> CompletableFuture<SendResult> sendAsync(N notification);

    /**
     * Envía un conjunto de notificaciones y devuelve los resultados.
     */
    <N extends Notification> List<SendResult> sendBatch(List<N> notifications);
}