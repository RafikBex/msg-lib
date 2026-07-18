package org.challenge.decorator.retry;

/**
 * Estrategias de espera entre reintentos de envío.
 */
public enum BackoffStrategy {

    /**
     * Mantiene el mismo tiempo de espera.
     *
     * Ejemplo:
     * 500 ms, 500 ms, 500 ms
     */
    FIXED,

    /**
     * Aumenta el tiempo de espera exponencialmente.
     *
     * Ejemplo con multiplicador 2:
     * 500 ms, 1 s, 2 s, 4 s
     */
    EXPONENTIAL
}