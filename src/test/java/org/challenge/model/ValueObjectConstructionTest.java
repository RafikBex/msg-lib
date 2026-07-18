package org.challenge.model;

import org.challenge.model.email.EmailAddress;
import org.challenge.model.push.DeviceToken;
import org.challenge.model.sms.PhoneNumber;
import org.challenge.notification.api.DeliveryStatus;
import org.challenge.notification.api.NotificationId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Verifica la construcción básica de los value objects y tipos
 * fundamentales del dominio de notificaciones.
 */
class ValueObjectConstructionTest {

    @Test
    void shouldCreateEmailAddress() {
        EmailAddress address = EmailAddress.of("user@example.com");

        assertNotNull(address);
        assertEquals("user@example.com", address.value());
    }

    @Test
    void shouldCreatePhoneNumber() {
        PhoneNumber phone = PhoneNumber.of("+593991234567");

        assertNotNull(phone);
        assertEquals("+593991234567", phone.value());
    }

    @Test
    void shouldCreateDeviceToken() {
        DeviceToken token = DeviceToken.of("device-token-abc123");

        assertNotNull(token);
        assertEquals("device-token-abc123", token.value());
    }

    @Test
    void shouldGenerateNotificationId() {
        NotificationId id = NotificationId.generate();

        assertNotNull(id);
        assertNotNull(id.value());
    }

    @Test
    void shouldCreateNotificationIdFromUuidString() {
        UUID uuid = UUID.randomUUID();
        NotificationId id = NotificationId.from(uuid.toString());

        assertEquals(uuid, id.value());
    }

    @Test
    void deliveryStatusShouldHaveExpectedValues() {
        assertNotNull(DeliveryStatus.ACCEPTED);
        assertNotNull(DeliveryStatus.SENT);
        assertNotNull(DeliveryStatus.FAILED);
    }
}
