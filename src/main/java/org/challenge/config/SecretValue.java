package org.challenge.config;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Envuelve un valor secreto para evitar que se imprima accidentalmente en logs.
 */
public final class SecretValue {

    private final String value;

    private SecretValue(String value) {
        Objects.requireNonNull(value, "Secret value cannot be null");

        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException("Secret value cannot be blank");
        }

        this.value = value;
    }

    /**
     * Crea un nuevo secreto a partir de una cadena.
     */
    public static SecretValue of(String value) {
        return new SecretValue(value);
    }

    /**
     * Debe utilizarse únicamente dentro del adaptador
     * que necesita enviar la credencial al proveedor.
     */
    public String reveal() {
        return value;
    }

    @Override
    public String toString() {
        return "********";
    }
}