package org.challenge.core;

import org.challenge.event.NotificationEvent;
import org.challenge.event.NotificationEventPublisher;
import org.challenge.event.NotificationFailedEvent;
import org.challenge.event.NotificationSentEvent;
import org.challenge.exception.NotificationException;
import org.challenge.notification.api.Notification;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.NotificationProvider;
import org.challenge.provider.ProviderRoutingStrategy;
import org.challenge.validation.NotificationValidator;

import java.util.Objects;

/**
 * Implementación por defecto de {@link ChannelPipeline}.
 * <p>
 * Encadena validación, selección de proveedor, envío y publicación de eventos de resultado.
 */
public final class DefaultChannelPipeline<N extends Notification> implements ChannelPipeline<N> {

    private final NotificationValidator<N> validator;
    private final ProviderRoutingStrategy<N> routingStrategy;
    private final NotificationEventPublisher eventPublisher;

    public DefaultChannelPipeline(NotificationValidator<N> validator, ProviderRoutingStrategy<N> routingStrategy, NotificationEventPublisher eventPublisher) {
        this.validator = Objects.requireNonNull(validator);
        this.routingStrategy = Objects.requireNonNull(routingStrategy);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public SendResult execute(N notification) {
        Objects.requireNonNull(notification, "Notification cannot be null");

        try {
            validator.validate(notification);

            NotificationProvider<N> provider = routingStrategy.selectProvider(notification);

            SendResult result = provider.send(notification);

            eventPublisher.publish(NotificationSentEvent.of(notification, result));

            return result;

        } catch (NotificationException exception) {
            eventPublisher.publish(NotificationFailedEvent.of(notification, exception));

            throw exception;
        }
    }
}