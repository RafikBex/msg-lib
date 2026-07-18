package org.challenge.decorator;

import org.challenge.notification.api.Notification;
import org.challenge.provider.NotificationProvider;

import java.util.Objects;

/**
 * Base para decorar un {@link NotificationProvider} sin modificar su nombre de proveedor.
 */
public abstract class NotificationProviderDecorator<N extends Notification> implements NotificationProvider<N> {

    protected final NotificationProvider<N> delegate;

    protected NotificationProviderDecorator(NotificationProvider<N> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "Delegate provider cannot be null");
    }

    @Override
    public String providerName() {
        return delegate.providerName();
    }
}