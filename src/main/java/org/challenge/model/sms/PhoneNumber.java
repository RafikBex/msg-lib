package org.challenge.model.sms;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa un número telefónico internacional.
 * <p>
 * +[código de país][número nacional]
 * <p>
 * Ejemplo:
 * +593991234567
 */
public record PhoneNumber(String value) {

    /*
     * se permite un máximo de 15 dígitos.
     *
     * Reglas:
     * - Debe comenzar con +
     * - El código de país no puede comenzar en 0
     * - Debe contener entre 8 y 15 dígitos
     */
    private static final Pattern E164_PATTERN =
            Pattern.compile("^\\+[1-9]\\d{7,14}$");

    public PhoneNumber {
        Objects.requireNonNull(
                value,
                "Phone number cannot be null"
        );

        value = normalize(value);

        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(
                    "Phone number cannot be blank"
            );
        }

        if (!E164_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Invalid phone number. Expected E.164 format, "
                            + "for example: +593991234567"
            );
        }
    }

    /**
     * Crea un número de teléfono a partir de una cadena.
     */
    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    /**
     * Elimina caracteres visuales comunes antes de validar.
     * <p>
     * Ejemplo:
     * +593 (99) 123-4567
     * se transforma en:
     * +593991234567
     */
    private static String normalize(String value) {
        return value.trim()
                .replace(" ", "")
                .replace("-", "")
                .replace("(", "")
                .replace(")", "");
    }

    @Override
    public String toString() {
        return value;
    }
}