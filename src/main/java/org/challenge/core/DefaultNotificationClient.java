package org.challenge.core;

import org.challenge.notification.api.Notification;
import org.challenge.notification.api.NotificationClient;
import org.challenge.notification.api.SendResult;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Implementación por defecto de {@link NotificationClient}.
 * <p>
 * Resuelve el pipeline apropiado para cada notificación y permite envíos síncronos,
 * asíncronos y en lote.
 */
public final class DefaultNotificationClient implements NotificationClient {

    private final HandlerRegistry registry;
    private final Executor executor;

    /**
     * Crea un nuevo cliente de notificaciones.
     *
     * @param registry registro que resuelve el pipeline correspondiente a cada tipo de notificación
     * @param executor executor utilizado para la ejecución de envíos asíncronos
     * @throws NullPointerException si {@code registry} o {@code executor} son {@code null}
     */
    public DefaultNotificationClient(HandlerRegistry registry, Executor executor) {
        this.registry = Objects.requireNonNull(registry);
        this.executor = Objects.requireNonNull(executor);
    }

    /**
     * Envía una notificación de forma síncrona.
     *
     * <p>Busca el {@link ChannelPipeline} registrado para el tipo de la notificación
     * y lo ejecuta en el hilo actual.</p>
     *
     * @param notification notificación a enviar
     * @param <N>          tipo de notificación
     * @return resultado del envío
     * @throws NullPointerException            si {@code notification} es {@code null}
     * @throws UnsupportedNotificationException si no hay un pipeline registrado para el tipo
     */
    @Override
    @SuppressWarnings("unchecked")
    public <N extends Notification> SendResult send(N notification) {
        Objects.requireNonNull(notification);

        Class<N> notificationClass = (Class<N>) notification.getClass();
        ChannelPipeline<N> pipeline = registry.findFor(notificationClass);

        return pipeline.execute(notification);
    }

    /**
     * Envía una notificación de forma asíncrona.
     *
     * <p>La ejecución se delega al {@link Executor} configurado en el constructor.</p>
     *
     * @param notification notificación a enviar
     * @param <N>          tipo de notificación
     * @return {@link CompletableFuture} que completará con el resultado del envío
     */
    @Override
    public <N extends Notification> CompletableFuture<SendResult> sendAsync(N notification) {
        return CompletableFuture.supplyAsync(() -> send(notification), executor);
    }

    /**
     * Envía un lote de notificaciones de forma síncrona y secuencial.
     *
     * <p>Cada notificación se envía mediante {@link #send(Notification)} y el resultado
     * se incluye en la lista devuelta en el mismo orden de entrada.</p>
     *
     * @param notifications lista de notificaciones a enviar
     * @param <N>           tipo de notificación
     * @return lista con los resultados de cada envío
     */
    @Override
    public <N extends Notification> List<SendResult> sendBatch(List<N> notifications) {
        return notifications.stream().map(this::send).toList();
    }
}