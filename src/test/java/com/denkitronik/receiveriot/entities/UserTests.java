package com.denkitronik.receiveriot.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


/**
 * Clase de pruebas para la entidad User
 */
@SpringBootTest(classes = User.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserTests {

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
    }

    @Test
    void testUserSettersAndGetters() {
        user.setId(1L);
        user.setUsername("Test User");

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getUsername());
    }

    @Test
    void testUserEquality() {
        user.setId(1L);
        user.setUsername("Test User");

        User anotherUser = new User();
        anotherUser.setId(1L);
        anotherUser.setUsername("Test User");

        assertEquals(user.getId(), anotherUser.getId());
        assertEquals(user.getUsername(), anotherUser.getUsername());

    }

    @Test
    void testUserHashCode() {
        user.setId(1L);
        user.setUsername("Test User");

        int hashCode = user.hashCode();
        assertNotEquals(0, hashCode); // Verifica que el valor no sea 0
    }
}
