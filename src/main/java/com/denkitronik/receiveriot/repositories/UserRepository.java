package com.denkitronik.receiveriot.repositories;

import com.denkitronik.receiveriot.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad que representa los usuarios MQTT
 */
public interface UserRepository extends JpaRepository<User, Long> {

    // Buscar un usuario por su nombre de usuario
    Optional<User> findByUsername(String username);
}
