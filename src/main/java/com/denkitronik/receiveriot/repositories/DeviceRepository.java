package com.denkitronik.receiveriot.repositories;

import com.denkitronik.receiveriot.entities.Location;
import com.denkitronik.receiveriot.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad que representa los dispositivos
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    // Buscar un dispositivo por su client-id y ubicación
    Optional<Device> findByClientIdAndLocation(String clientId, Location location);
}

