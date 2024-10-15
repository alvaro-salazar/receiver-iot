package com.denkitronik.receiveriot.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Prueba de HyperTableService usando Testcontainers para TimescaleDB.
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HyperTableServiceTests {

    // Contenedor de TimescaleDB usando Testcontainers
    private static final PostgreSQLContainer<?> timescaleDB = new PostgreSQLContainer<>(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                    .asCompatibleSubstituteFor("postgres")
    );

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HyperTableService hyperTableService;

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
    void setupDatabase() {
        // Crear la tabla "data" antes de cada prueba
        jdbcTemplate.execute("DROP TABLE IF EXISTS data;");
        jdbcTemplate.execute("""
                CREATE TABLE data (
                    unix_time BIGINT NOT NULL,
                    variable_value FLOAT,
                    base_time TIMESTAMPTZ,
                    device_id BIGINT,
                    variable_id BIGINT
                );
                """);
    }

    @Test
    void testCreateHypertableIfNotExists_Success() {
        // Ejecutar el método para crear la hypertable
        assertDoesNotThrow(() -> hyperTableService.createHypertableIfNotExists());

        // Verificar que la tabla se haya convertido en una hypertable
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM timescaledb_information.hypertables WHERE hypertable_name = 'data';",
                Integer.class
        );
        assertEquals(1, count, "La tabla 'data' debe haberse convertido en una hypertable");
    }

    @Test
    void testCreateHypertableIfNotExists_AlreadyExists() {
        // Crear la hypertable manualmente
        jdbcTemplate.execute(
                "SELECT create_hypertable('data', 'unix_time', if_not_exists => TRUE, chunk_time_interval => 86400000);"
        );

        // Ejecutar el método, no debe lanzar excepción ni duplicar la hypertable
        assertDoesNotThrow(() -> hyperTableService.createHypertableIfNotExists());

        // Verificar que sigue existiendo solo una hypertable
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM timescaledb_information.hypertables WHERE hypertable_name = 'data';",
                Integer.class
        );
        assertEquals(1, count, "Debe existir una única hypertable 'data'");
    }
}
