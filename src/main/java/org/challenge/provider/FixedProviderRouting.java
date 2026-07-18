package org.challenge.provider;

import org.challenge.notification.api.Notification;

import java.util.Objects;

/**
 * Estrategia de ruteo que siempre devuelve el mismo proveedor configurado.
 */
public final class FixedProviderRouting<N extends Notification> implements ProviderRoutingStrategy<N> {

    private final NotificationProvider<N> provider;

    public FixedProviderRouting(NotificationProvider<N> provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public NotificationProvider<N> selectProvider(N notification) {
        //TODO: la notificación no se utiliza para la selección; una estrategia real podría usar el contenido o metadatos.
        return provider;
    }

}