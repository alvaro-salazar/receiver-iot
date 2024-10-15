package com.denkitronik.receiveriot.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.denkitronik.receiveriot.entities.Device;
import com.denkitronik.receiveriot.entities.Location;
import com.denkitronik.receiveriot.entities.User;
import com.denkitronik.receiveriot.repositories.DeviceRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DeviceServiceTests {

    @MockBean
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceService deviceService;

    private Device device;
    private User user;
    private Location location;

    // Contenedor de TimescaleDB basado en PostgreSQL
    private static final PostgreSQLContainer<?> timescaleDB = new PostgreSQLContainer<>(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                    .asCompatibleSubstituteFor("postgres")
    );

    // Configurar las propiedades de conexión usando Testcontainers
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", timescaleDB::getJdbcUrl);
        registry.add("spring.datasource.username", timescaleDB::getUsername);
        registry.add("spring.datasource.password", timescaleDB::getPassword);
    }

    @BeforeAll
    static void startContainer() {
        timescaleDB.start();
    }

    @BeforeEach
    void setupTest() {
        // Crear usuario de prueba
        user = new User();
        user.setId(1L);
        user.setUsername("Test User");

        // Crear ubicación de prueba
        location = new Location();
        location.setId(1L);
        location.setCity("Bogotá");
        location.setState("Cundinamarca");
        location.setCountry("Colombia");
        location.setLatitude(4.7110);
        location.setLongitude(-74.0721);

        // Crear dispositivo de prueba
        device = new Device();
        device.setId(1L);
        device.setClientId("device-123");
        device.setUser(user);
        device.setLocation(location);
    }

    @Test
    void testGetOrCreateDevice_ExistingDevice() {
        when(deviceRepository.findByClientIdAndLocation("device-123", location))
                .thenReturn(Optional.of(device));

        Device result = deviceService.getOrCreateDevice("device-123", user, location);

        assertNotNull(result);
        assertEquals("device-123", result.getClientId());
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void testGetOrCreateDevice_NewDevice() {
        when(deviceRepository.findByClientIdAndLocation("device-456", location))
                .thenReturn(Optional.empty());

        when(deviceRepository.save(any(Device.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Device result = deviceService.getOrCreateDevice("device-456", user, location);

        assertNotNull(result);
        assertEquals("device-456", result.getClientId());
        assertEquals(user, result.getUser());
        assertEquals(location, result.getLocation());
        verify(deviceRepository, times(1)).save(any(Device.class));
    }
}


