package com.denkitronik.receiveriot.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Clase de pruebas para la entidad Measurement
 */
@SpringBootTest(classes = Measurement.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MeasurementTests {

    private Measurement measurement;

    @BeforeEach
    void setup() {
        measurement = new Measurement();
    }

    @Test
    void testMeasurementSettersAndGetters() {
        measurement.setId(1L);
        measurement.setName("Temperature");

        assertEquals(1L, measurement.getId());
        assertEquals("Temperature", measurement.getName());
    }

    @Test
    void testMeasurementEquality() {
        measurement.setId(1L);
        measurement.setName("Temperature");

        Measurement anotherMeasurement = new Measurement();
        anotherMeasurement.setId(1L);
        anotherMeasurement.setName("Temperature");

        assertEquals(measurement.getId(), anotherMeasurement.getId());
        assertEquals(measurement.getName(), anotherMeasurement.getName());
    }

    @Test
    void testMeasurementHashCode() {
        Measurement tempMeasurement = new Measurement();
        tempMeasurement.setId(1L);
        tempMeasurement.setName("Temperature");

        assertNotEquals(0, tempMeasurement.hashCode(), "Hash code should not be zero");
    }
}
