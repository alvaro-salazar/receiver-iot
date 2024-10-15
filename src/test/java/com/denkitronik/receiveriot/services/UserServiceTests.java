package com.denkitronik.receiveriot.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.denkitronik.receiveriot.entities.User;
import com.denkitronik.receiveriot.repositories.UserRepository;
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

/**
 * Pruebas unitarias para el servicio de usuarios
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTests {

    @MockBean
    private UserRepository userRepository; // Mock del repositorio de usuarios

    @Autowired
    private UserService userService; // Servicio de usuarios

    private User user; // Usuario de prueba

    /**
     * Contenedor de base de datos
     */
    private static final PostgreSQLContainer<?> timescaleDB = new PostgreSQLContainer<>(
            DockerImageName.parse("timescale/timescaledb:latest-pg14")
                    .asCompatibleSubstituteFor("postgres")
    );

    /**
     * Configuración de las propiedades de la base de datos
     */
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", timescaleDB::getJdbcUrl);
        registry.add("spring.datasource.username", timescaleDB::getUsername);
        registry.add("spring.datasource.password", timescaleDB::getPassword);
    }

    /**
     * Inicialización del contenedor de base de datos
     */
    @BeforeAll
    static void startContainer() {
        timescaleDB.start();
    }

    /**
     * Configuración de las pruebas
     */
    @BeforeEach
    void setupTest() {
        user = new User();
        user.setId(1L);
        user.setUsername("Test User");
    }

    /**
     * Test para el caso en el que se crea u obtiene un usuario existente
     */
    @Test
    void testGetOrCreateUser_ExistingUser() {
        when(userRepository.findByUsername("Test User")).thenReturn(Optional.of(user));

        User result = userService.getUser("Test User");
        System.out.println(result.getId()+" " + result.getUsername());
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getUsername());
        verify(userRepository, never()).save(any(User.class));
    }

    /**
     * Test para el caso en el que se crea u obtiene un nuevo usuario
     */
    @Test
    void testGetOrCreateUser_NewUser() {
        when(userRepository.findByUsername("New User")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.getUser("New User");

        assertNotNull(result);
        assertEquals("New User", result.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }
}
