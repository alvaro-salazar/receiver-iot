package com.denkitronik.receiveriot.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

/**
 * Entidad que representa un mensaje MQTT recibido por el broker.
 */
@Setter
@Getter
@Entity
public class MqttMessageEntity {
    // Getters y Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String topic;
    private String message;
    private LocalDateTime timestamp;
}
