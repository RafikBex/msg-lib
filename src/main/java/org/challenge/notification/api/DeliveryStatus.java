package org.challenge.notification.api;

/**
 * Estados posibles del resultado de entrega de una notificación.
 */
public enum DeliveryStatus {

    /**
     * La notificación fue aceptada por el proveedor pero aún no se confirma el envío.
     */
    ACCEPTED,

    /**
     * La notificación fue enviada exitosamente.
     */
    SENT,

    /**
     * La notificación no pudo ser entregada.
     */
    FAILED
}