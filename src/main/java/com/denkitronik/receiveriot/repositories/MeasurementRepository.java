package com.denkitronik.receiveriot.repositories;

import com.denkitronik.receiveriot.entities.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad que representa las variables de las mediciones
 */
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    // Buscar una medida por su nombre
    Optional<Measurement> findByName(String name);
}
