package com.denkitronik.receiveriot.repositories;

import com.denkitronik.receiveriot.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad que representa la ubicación de un dispositivo
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

    // Buscar una ubicación por ciudad, estado y país
    Optional<Location> findByCityAndStateAndCountry(String city, String state, String country);
}
