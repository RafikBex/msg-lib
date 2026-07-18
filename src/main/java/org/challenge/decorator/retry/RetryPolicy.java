package org.challenge.decorator.retry;

import java.time.Duration;
import java.util.Objects;

/**
 * Configuración de reintentos para el decorador {@link org.challenge.decorator.RetryingNotificationProvider}.
 */
public record RetryPolicy(int maxAttempts, Duration initialDelay, Duration maxDelay, BackoffStrategy backoffStrategy,
                          double multiplier) {

    private static final int DEFAULT_MAX_ATTEMPTS = 3;
    private static final Duration DEFAULT_INITIAL_DELAY = Duration.ofMillis(500);
    private static final Duration DEFAULT_MAX_DELAY = Duration.ofSeconds(10);
    private static final double DEFAULT_MULTIPLIER = 2.0;

    public RetryPolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("Maximum attempts must be at least 1");
        }
        initialDelay = requireNonNegative(initialDelay, "Initial delay");
        maxDelay = requireNonNegative(maxDelay, "Maximum delay");
        backoffStrategy = Objects.requireNonNull(backoffStrategy, "Backoff strategy cannot be null");

        if (maxDelay.compareTo(initialDelay) < 0) {
            throw new IllegalArgumentException("Maximum delay cannot be less than initial delay");
        }

        if (multiplier < 1.0) {
            throw new IllegalArgumentException("Retry multiplier must be greater than or equal to 1");
        }

        if (backoffStrategy == BackoffStrategy.EXPONENTIAL && multiplier == 1.0) {
            throw new IllegalArgumentException("Exponential backoff multiplier must be greater than 1");
        }
    }

    /**
     * Política sin reintentos.
     * <p>
     * Solo se ejecuta el intento inicial.
     */
    public static RetryPolicy noRetry() {
        return new RetryPolicy(1, Duration.ZERO, Duration.ZERO, BackoffStrategy.FIXED, 1.0);
    }

    /**
     * Crea una política con una espera fija.
     * <p>
     * Ejemplo:
     * fixedDelay(3, Duration.ofMillis(500))
     * <p>
     * Ejecuta hasta 3 intentos con 500 ms entre cada uno.
     */
    public static RetryPolicy fixedDelay(int maxAttempts, Duration delay) {
        return new RetryPolicy(maxAttempts, delay, delay, BackoffStrategy.FIXED, 1.0);
    }

    /**
     * Crea una política de espera exponencial.
     */
    public static RetryPolicy exponentialBackoff(int maxAttempts, Duration initialDelay, Duration maxDelay) {
        return new RetryPolicy(maxAttempts, initialDelay, maxDelay, BackoffStrategy.EXPONENTIAL, DEFAULT_MULTIPLIER);
    }

    /**
     * Crea una política de espera exponencial con multiplicador
     * personalizado.
     */
    public static RetryPolicy exponentialBackoff(int maxAttempts, Duration initialDelay, Duration maxDelay, double multiplier) {
        return new RetryPolicy(maxAttempts, initialDelay, maxDelay, BackoffStrategy.EXPONENTIAL, multiplier);
    }

    /**
     * Política predeterminada de la librería.
     */
    public static RetryPolicy defaultPolicy() {
        return exponentialBackoff(DEFAULT_MAX_ATTEMPTS, DEFAULT_INITIAL_DELAY, DEFAULT_MAX_DELAY, DEFAULT_MULTIPLIER);
    }

    /**
     * Calcula cuánto esperar antes de un determinado intento.
     * <p>
     * attemptNumber empieza en 1.
     * <p>
     * - Intento 1: Duration.ZERO
     * - Intento 2: initialDelay
     * - Intento 3: siguiente valor del backoff
     */
    public Duration delayBeforeAttempt(int attemptNumber) {
        if (attemptNumber < 1) {
            throw new IllegalArgumentException("Attempt number must be at least 1");
        }

        if (attemptNumber == 1) {
            return Duration.ZERO;
        }

        if (backoffStrategy == BackoffStrategy.FIXED) {
            return initialDelay;
        }

        int retryIndex = attemptNumber - 2;

        double calculatedMillis = initialDelay.toMillis() * Math.pow(multiplier, retryIndex);

        long safeMillis;

        if (calculatedMillis >= Long.MAX_VALUE) {
            safeMillis = Long.MAX_VALUE;
        } else {
            safeMillis = (long) calculatedMillis;
        }

        Duration calculatedDelay = Duration.ofMillis(safeMillis);

        return calculatedDelay.compareTo(maxDelay) > 0 ? maxDelay : calculatedDelay;
    }

    /**
     * Determina si se debe realizar otro reintento después de un intento fallido.
     *
     * @param attemptNumber número del intento que acaba de ejecutarse (empieza en 1)
     * @return {@code true} si aún quedan intentos disponibles; {@code false} en caso contrario
     * @throws IllegalArgumentException si {@code attemptNumber} es menor que 1
     */
    public boolean shouldRetryAfter(int attemptNumber) {
        if (attemptNumber < 1) {
            throw new IllegalArgumentException("Attempt number must be at least 1");
        }

        return attemptNumber < maxAttempts;
    }

    /**
     * Verifica que una duración no sea {@code null} ni negativa.
     *
     * @param duration  duración a validar
     * @param fieldName nombre del campo, usado en los mensajes de error
     * @return la misma duración recibida
     * @throws NullPointerException     si {@code duration} es {@code null}
     * @throws IllegalArgumentException si {@code duration} es negativa
     */
    private static Duration requireNonNegative(Duration duration, String fieldName) {
        Objects.requireNonNull(duration, fieldName + " cannot be null");

        if (duration.isNegative()) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }

        return duration;
    }
}