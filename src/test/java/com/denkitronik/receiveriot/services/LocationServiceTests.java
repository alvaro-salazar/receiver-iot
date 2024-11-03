package com.denkitronik.receiveriot.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.denkitronik.receiveriot.entities.Location;
import com.denkitronik.receiveriot.repositories.LocationRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LocationServiceTests {

    @MockBean
    private LocationRepository locationRepository;

    @Autowired
    private LocationService locationService;

    @SpyBean
    private RestTemplate restTemplate;

    private Location location;

    private static final PostgreSQLContainer<?> timescaleDB = new PostgreSQLContainer<>(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                    .asCompatibleSubstituteFor("postgres")
    );

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
        location = new Location();
        location.setCity("Bogotá");
        location.setState("Cundinamarca");
        location.setCountry("Colombia");

        when(locationRepository.findByCityAndStateAndCountry("TestCity", "TestState", "TestCountry"))
                .thenReturn(Optional.empty());
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void testGetOrCreateLocation_ExistingLocation() {
        when(locationRepository.findByCityAndStateAndCountry("Bogotá", "Cundinamarca", "Colombia"))
                .thenReturn(Optional.of(location));

        Location result = locationService.getOrCreateLocation("Bogotá", "Cundinamarca", "Colombia");

        assertNotNull(result);
        assertEquals("Bogotá", result.getCity());
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void testGetOrCreateLocation_NewLocation() {
        when(locationRepository.findByCityAndStateAndCountry("Medellín", "Antioquia", "Colombia"))
                .thenReturn(Optional.empty());
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Location result = locationService.getOrCreateLocation("Medellín", "Antioquia", "Colombia");

        assertNotNull(result);
        assertEquals("Medellín", result.getCity());
    }


    @Test
    void testGetCoordinatesFromApi_NullLatitudeLongitude() {
        // Simula la respuesta de la API con valores null en las claves "latt" y "longt"
        Map<String, Object> apiResponse = new HashMap<>();
        apiResponse.put("latt", null);
        apiResponse.put("longt", null);

        doReturn(apiResponse)
                .when(restTemplate)
                .getForObject(anyString(), eq(Map.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            locationService.getOrCreateLocation("TestCity", "TestState", "TestCountry");
        });

        assertFalse(exception.getMessage().contains("No se pudo obtener las coordenadas para la ciudad"),
                "El mensaje de la excepción debe indicar que no se pudo obtener las coordenadas");
    }

}
