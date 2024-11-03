package com.denkitronik.receiveriot;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas para la aplicación ReceiverIotApplication
 * Esta clase se encarga de verificar que el contexto de la aplicación se cargue correctamente
 * y que el contenedor de TimescaleDB esté en ejecución
 * DirtiesContext se utiliza para reiniciar el contexto de Spring después de cada prueba
 * DynamicPropertySource se utiliza para configurar las propiedades de la base de datos de forma dinámica
 * antes de que se cargue el contexto de Spring
 */
@SpringBootTest(classes = ReceiverIotApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReceiverIotApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * Contenedor de TimescaleDB que se utilizará para las pruebas
     * Se utiliza la imagen de TimescaleDB compatible con PostgreSQL 14
     * Se inicia el contenedor antes de todas las pruebas
     */
    private static final PostgreSQLContainer<?> timescaleDB = new PostgreSQLContainer<>(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                    .asCompatibleSubstituteFor("postgres")
    );

    /**
     * Inicia el contenedor de TimescaleDB antes de todas las pruebas
     */
    @BeforeAll
    static void setup() {
        timescaleDB.start();  // Inicia el contenedor antes de todas las pruebas
    }

    /**
     * Configura las propiedades de la base de datos de forma dinámica antes de que se cargue el contexto de Spring
     * @param registry Registro de propiedades dinámicas
     */
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", timescaleDB::getJdbcUrl);
        registry.add("spring.datasource.username", timescaleDB::getUsername);
        registry.add("spring.datasource.password", timescaleDB::getPassword);
    }

    /**
     * Verifica que el contenedor de TimescaleDB esté en ejecución
     */
    @Test
    void testContainerIsRunning() {
        assertTrue(timescaleDB.isRunning(), "El contenedor de TimescaleDB debería estar ejecutándose");
    }

    /**
     * Verifica que el contexto de la aplicación se cargue correctamente
     * Esta prueba se ejecuta después de que el contenedor de TimescaleDB esté en ejecución
     * y se asegura de que el contexto de la aplicación se cargue correctamente
     * y que las entidades de la aplicación se hayan mapeado correctamente a las tablas de la base de datos
     */
    @Test
    void contextLoads() {
        // Verifica que el contexto de la aplicación se cargue correctamente
        assertNotNull(applicationContext, "El contexto de la aplicación no debería ser nulo");
    }

    /**
     * Prueba que verifica que los servicios de la aplicación se hayan mapeado correctamente
     */
    @Test
    void verifyApplicationServicesLoaded() {
        // Verifica que los servicios se hayan cargado correctamente
        assertTrue(applicationContext.containsBean("dataService"), "El contexto debería contener el bean 'dataService'");
        assertTrue(applicationContext.containsBean("locationService"), "El contexto debería contener el bean 'locationService'");
        assertTrue(applicationContext.containsBean("measurementService"), "El contexto debería contener el bean 'measurementService'");
        assertTrue(applicationContext.containsBean("deviceService"), "El contexto debería contener el bean 'deviceService'");
        assertTrue(applicationContext.containsBean("mqttController"), "El contexto debería contener el bean 'mqttController'");
        assertTrue(applicationContext.containsBean("userService"), "El contexto debería contener el bean 'userService'");
        assertTrue(applicationContext.containsBean("hyperTableService"), "El contexto debería contener el bean 'hyperTableService'");
    }

}
