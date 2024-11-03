package com.denkitronik.receiveriot.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.ZoneId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas para la entidad DataEntity
 */
@SpringBootTest(classes = DataEntity.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DataEntityTests {

    private DataEntity dataEntity;

    @BeforeEach
    void setUp() {
        dataEntity = new DataEntity();
    }

    @Test
    void testPrePersist() {
        // Ejecutar el método @PrePersist
        dataEntity.prePersist();

        // Verificar que el unix_time fue inicializado
        assertNotNull(dataEntity.getUnixTime(), "unix_time no debería ser nulo");
        assertTrue(dataEntity.getUnixTime() > 0, "unix_time debería ser mayor a 0");

        // Verificar que base_time fue inicializado correctamente con la zona horaria esperada
        assertNotNull(dataEntity.getBaseTime(), "base_time no debería ser nulo");
        assertEquals(ZoneId.of("America/Bogota"), dataEntity.getBaseTime().getZone(),
                "La zona horaria debería ser America/Bogota");
    }

    @Test
    void testSetAndGetVariable() {
        Measurement mockMeasurement = mock(Measurement.class);

        dataEntity.setVariable(mockMeasurement);

        // Verificar que se asoció correctamente la variable
        assertEquals(mockMeasurement, dataEntity.getVariable(),
                "La variable debería ser la misma que se estableció");
    }

    @Test
    void testSetAndGetDevice() {
        Device mockDevice = mock(Device.class);

        dataEntity.setDevice(mockDevice);

        // Verificar que se asoció correctamente el dispositivo
        assertEquals(mockDevice, dataEntity.getDevice(),
                "El dispositivo debería ser el mismo que se estableció");
    }

    @Test
    void testVariableValue() {
        dataEntity.setVariableValue(10.5f);

        // Verificar que el valor de la variable se estableció correctamente
        assertEquals(10.5f, dataEntity.getVariableValue(), 0.001,
                "El valor de la variable debería ser 10.5");
    }
}
