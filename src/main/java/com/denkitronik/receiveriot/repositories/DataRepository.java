package com.denkitronik.receiveriot.repositories;

import com.denkitronik.receiveriot.entities.DataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repositorio JPA para la entidad que representa los datos de las mediciones de los dispositivos
 */
public interface DataRepository extends JpaRepository<DataEntity, Long> {
}

