package com.denkitronik.receiveriot.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.denkitronik.receiveriot.entities.DataEntity;
import com.denkitronik.receiveriot.entities.Device;
import com.denkitronik.receiveriot.entities.Measurement;
import com.denkitronik.receiveriot.repositories.DataRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import java.time.ZonedDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DataServiceTests {

    @MockBean
    private DataRepository dataRepository;

    @Autowired
    private DataService dataService;

    private DataEntity dataEntity;
    private Device device;
    private Measurement measurement;
    private ZonedDateTime timestamp;

    // Contenedor de TimescaleDB
    private static final PostgreSQLContainer<?> timescaleDB = new PostgreSQLContainer<>(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                    .asCompatibleSubstituteFor("postgres")
    );

    // Registrar las propiedades dinámicas para conectar a la DB
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", timescaleDB::getJdbcUrl);
        registry.add("spring.datasource.username", timescaleDB::getUsername);
        registry.add("spring.datasource.password", timescaleDB::getPassword);
    }

    @BeforeAll
    static void startContainer() {
        timescaleDB.start(); // Iniciar el contenedor
    }

    @BeforeEach
    void setupTest() {
        // Crear dispositivo de prueba
        device = new Device();
        device.setId(1L);
        device.setClientId("device-001");

        // Crear variable de medición
        measurement = new Measurement();
        measurement.setId(1L);
        measurement.setName("Temperature");

        // Crear marca de tiempo
        timestamp = ZonedDateTime.now();

        // Crear entidad de datos
        dataEntity = new DataEntity();
        dataEntity.setVariableValue(23.5f);
        dataEntity.setDevice(device);
        dataEntity.setVariable(measurement);
        dataEntity.setBaseTime(timestamp);
    }

    @Test
    void testCreateData_Success() {
        // Simular que el metodo save() devuelve la entidad guardada
        when(dataRepository.save(any(DataEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Llamar al servicio para crear los datos
        dataService.createData(23.5f, device, measurement, timestamp);

        // Capturar el argumento pasado al metodo save()
        ArgumentCaptor<DataEntity> dataCaptor = ArgumentCaptor.forClass(DataEntity.class);
        verify(dataRepository, times(1)).save(dataCaptor.capture());

        // Verificar los valores de la entidad guardada
        DataEntity savedData = dataCaptor.getValue();
        assertEquals(23.5f, savedData.getVariableValue());
        assertEquals(device, savedData.getDevice());
        assertEquals(measurement, savedData.getVariable());
        assertEquals(timestamp, savedData.getBaseTime());
    }

    @Test
    void testCreateData_ValidationFailure() {
        // Llamar al servicio con un valor nulo y verificar que se lanza una excepción
        assertThrows(IllegalArgumentException.class, () ->
                dataService.createData(0.0f, null, measurement, timestamp)
        );

        // Verificar que no se haya llamado al metodo save()
        verify(dataRepository, never()).save(any(DataEntity.class));
    }
}

