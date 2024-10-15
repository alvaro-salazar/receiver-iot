package com.denkitronik.receiveriot.repositories;

import com.denkitronik.receiveriot.entities.MqttMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad que representa los mensajes MQTT
 */
public interface MqttMessageRepository extends JpaRepository<MqttMessageEntity, Long> {
}
