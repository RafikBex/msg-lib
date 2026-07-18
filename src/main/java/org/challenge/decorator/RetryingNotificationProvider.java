package org.challenge.decorator;


import org.challenge.decorator.retry.RetryPolicy;
import org.challenge.decorator.retry.Sleeper;
import org.challenge.decorator.retry.ThreadSleeper;
import org.challenge.exception.NotificationDeliveryException;
import org.challenge.notification.api.Notification;
import org.challenge.notification.api.SendResult;
import org.challenge.provider.NotificationProvider;

import java.time.Duration;
import java.util.Objects;

/**
 * Decorador que reintenta el envío de una notificación cuando el proveedor
 * lanza una excepción reintentable.
 */
public final class RetryingNotificationProvider<N extends Notification> extends NotificationProviderDecorator<N> {

    private final RetryPolicy retryPolicy;
    private final Sleeper sleeper;

    public RetryingNotificationProvider(NotificationProvider<N> delegate, RetryPolicy retryPolicy) {
        this(delegate, retryPolicy, new ThreadSleeper());
    }

    /**
     * Constructor que permite inyectar un {@link Sleeper} alternativo,
     * útil para pruebas unitarias.
     */
    public RetryingNotificationProvider(NotificationProvider<N> delegate, RetryPolicy retryPolicy, Sleeper sleeper) {
        super(delegate);

        this.retryPolicy = Objects.requireNonNull(retryPolicy, "Retry policy cannot be null");
        this.sleeper = Objects.requireNonNull(sleeper, "Sleeper cannot be null");
    }

    @Override
    public SendResult send(N notification) {
        NotificationDeliveryException lastException = null;

        for (int attempt = 1; attempt <= retryPolicy.maxAttempts(); attempt++) {
            try {
                waitBeforeAttempt(attempt);

                return delegate.send(notification);

            } catch (NotificationDeliveryException exception) {
                lastException = exception;

                if (!exception.isRetryable()) {
                    throw exception;
                }

                if (!retryPolicy.shouldRetryAfter(attempt)) {
                    throw exception;
                }
            }
        }

        /*
         * El flujo normalmente nunca llega aquí, pero se mantiene
         * como protección defensiva.
         */
        throw NotificationDeliveryException.nonRetryable(providerName(), notification.id(), "Retry execution finished without a result", lastException);
    }

    private void waitBeforeAttempt(int attempt) {
        Duration delay = retryPolicy.delayBeforeAttempt(attempt);

        if (delay.isZero()) {
            return;
        }

        try {
            sleeper.sleep(delay);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw NotificationDeliveryException.nonRetryable(providerName(), null, "Retry execution was interrupted", exception);
        }
    }
}