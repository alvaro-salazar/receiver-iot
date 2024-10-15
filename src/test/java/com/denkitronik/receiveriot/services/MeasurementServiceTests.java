package com.denkitronik.receiveriot.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.denkitronik.receiveriot.entities.Measurement;
import com.denkitronik.receiveriot.repositories.MeasurementRepository;
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
class MeasurementServiceTests {

    @MockBean
    private MeasurementRepository measurementRepository;

    @Autowired
    private MeasurementService measurementService;

    private Measurement measurement;

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
        measurement = new Measurement();
        measurement.setName("Temperature");
    }

    @Test
    void testGetOrCreateMeasurement_ExistingMeasurement() {
        when(measurementRepository.findByName("Temperature")).thenReturn(Optional.of(measurement));

        Measurement result = measurementService.getOrCreateMeasurement("Temperature");

        assertNotNull(result);
        assertEquals("Temperature", result.getName());
        verify(measurementRepository, never()).save(any(Measurement.class));
    }

    @Test
    void testGetOrCreateMeasurement_NewMeasurement() {
        when(measurementRepository.findByName("Humidity")).thenReturn(Optional.empty());
        when(measurementRepository.save(any(Measurement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Measurement result = measurementService.getOrCreateMeasurement("Humidity");

        assertNotNull(result);
        assertEquals("Humidity", result.getName());
        verify(measurementRepository, times(1)).save(any(Measurement.class));
    }
}
