package com.denkitronik.receiveriot.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Clase de pruebas para la entidad Device
 */
@SpringBootTest(classes = Device.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeviceTests {

    private Device device;

    @BeforeEach
    void setup() {
        device = new Device();
    }

    @Test
    void testDeviceSettersAndGetters() {
        device.setId(1L);
        device.setClientId("device-123");

        assertEquals(1L, device.getId());
        assertEquals("device-123", device.getClientId());
    }

    @Test
    void testDeviceEquality() {
        device.setId(1L);
        device.setClientId("device-123");

        Device anotherDevice = new Device();
        anotherDevice.setId(1L);
        anotherDevice.setClientId("device-123");

        assertEquals(device.getId(), anotherDevice.getId());
        assertEquals(device.getClientId(), anotherDevice.getClientId());
    }

    @Test
    void testDeviceHashCodeDifferenceWithoutEquals() {
        Device device1 = new Device();
        device1.setId(1L);
        device1.setClientId("device-123");

        Device device2 = new Device();
        device2.setId(1L);
        device2.setClientId("device-123");

        // Verifica que son objetos diferentes
        assertNotSame(device1, device2, "Los dos dispositivos no deben ser la misma instancia");

        // Hashes diferentes, lo cual es válido al no sobrescribir hashCode()
        assertNotEquals(device1.hashCode(), device2.hashCode(),
                "Es posible que dos objetos diferentes tengan hashCodes distintos");
    }
}
