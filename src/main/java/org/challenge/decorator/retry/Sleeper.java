package org.challenge.decorator.retry;

import java.time.Duration;

/**
 * Abstracción para pausar la ejecución durante un reintento.
 */
@FunctionalInterface
public interface Sleeper {

    /**
     * Pausa el hilo actual durante la duración indicada.
     */
    void sleep(Duration duration) throws InterruptedException;
}