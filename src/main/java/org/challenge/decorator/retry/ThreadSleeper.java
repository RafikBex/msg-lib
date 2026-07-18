package org.challenge.decorator.retry;

import java.time.Duration;
import java.util.Objects;

/**
 * Implementación de {@link Sleeper} que usa {@link Thread#sleep}.
 */
public final class ThreadSleeper implements Sleeper {

    @Override
    public void sleep(Duration duration)
            throws InterruptedException {

        Objects.requireNonNull(
                duration,
                "Sleep duration cannot be null"
        );

        if (duration.isNegative()) {
            throw new IllegalArgumentException(
                    "Sleep duration cannot be negative"
            );
        }

        Thread.sleep(duration);
    }
}