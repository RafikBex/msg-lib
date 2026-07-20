package org.challenge.decorator;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(RetryingNotificationProvider.class);

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
            logger.info(
                    "[{}] Delivery attempt {} of {}",
                    providerName(),
                    attempt,
                    retryPolicy.maxAttempts()
            );

            try {
                waitBeforeAttempt(attempt);

                SendResult result = delegate.send(notification);

                logger.info(
                        "[{}] Delivery succeeded on attempt {}",
                        providerName(),
                        attempt
                );

                return result;

            } catch (NotificationDeliveryException exception) {
                lastException = exception;

                if (!exception.isRetryable()) {
                    logger.warn(
                            "[{}] Non-retryable error on attempt {}: {}",
                            providerName(),
                            attempt,
                            exception.getMessage()
                    );

                    throw exception;
                }

                if (!retryPolicy.shouldRetryAfter(attempt)) {
                    logger.error(
                            "[{}] All {} delivery attempts exhausted",
                            providerName(),
                            retryPolicy.maxAttempts()
                    );

                    throw exception;
                }

                Duration nextDelay = retryPolicy.delayBeforeAttempt(attempt + 1);

                logger.warn(
                        "[{}] Delivery attempt {} failed, retrying in {} ms: {}",
                        providerName(),
                        attempt,
                        nextDelay.toMillis(),
                        exception.getMessage()
                );
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