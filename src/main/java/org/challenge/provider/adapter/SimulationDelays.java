package org.challenge.provider.adapter;

import org.apache.logging.log4j.Logger;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilidad para simular latencia de red en los proveedores mock.
 */
public final class SimulationDelays {

    private static final int MIN_MILLIS = 1_000;
    private static final int MAX_MILLIS = 5_000;

    private SimulationDelays() {
    }

    /**
     * Pausa el hilo actual durante un tiempo aleatorio entre 1 y 5 segundos
     * e informa el tiempo de espera mediante el logger recibido.
     *
     * @param logger logger del proveedor que realiza la llamada
     */
    public static void simulateNetworkLatency(Logger logger) {
        int delayMillis = ThreadLocalRandom.current()
                .nextInt(MIN_MILLIS, MAX_MILLIS + 1);

        logger.info("Simulating provider latency: {} ms", delayMillis);

        try {
            Thread.sleep(delayMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(
                    "Latency simulation interrupted",
                    exception
            );
        }
    }
}
