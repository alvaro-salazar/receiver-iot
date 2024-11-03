package com.denkitronik.receiveriot.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Clase de pruebas para la entidad Location
 */
@SpringBootTest(classes = Location.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LocationTests {

    private Location location;

    @BeforeEach
    void setup() {
        location = new Location();
    }

    @Test
    void testLocationSettersAndGetters() {
        location.setId(1L);
        location.setCity("Bogotá");
        location.setState("Cundinamarca");
        location.setCountry("Colombia");
        location.setLatitude(4.7110);
        location.setLongitude(-74.0721);

        assertEquals(1L, location.getId());
        assertEquals("Bogotá", location.getCity());
        assertEquals("Cundinamarca", location.getState());
        assertEquals("Colombia", location.getCountry());
        assertEquals(4.7110, location.getLatitude());
        assertEquals(-74.0721, location.getLongitude());
    }

    @Test
    void testLocationEquality() {
        location.setId(1L);
        location.setCity("Bogotá");
        location.setState("Cundinamarca");
        location.setCountry("Colombia");
        location.setLatitude(4.7110);
        location.setLongitude(-74.0721);

        Location anotherLocation = new Location();
        anotherLocation.setId(1L);
        anotherLocation.setCity("Bogotá");
        assertEquals(location.getId(), anotherLocation.getId());
        assertEquals(location.getCity(), anotherLocation.getCity());
    }

    @Test
    void testLocationHashCode() {
        location.setId(1L);
        location.setCity("Bogotá");

        int hashCode = location.hashCode();
        assertNotEquals(0, hashCode);
    }
}
