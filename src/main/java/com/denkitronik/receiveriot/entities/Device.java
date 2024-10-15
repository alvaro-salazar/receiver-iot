package com.denkitronik.receiveriot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase que representa la entidad Device en la base de datos
 */
@Getter
@Setter
@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // A un dispositivo se le asigna un client-id unico
    @Column(name="client-id", unique = true, nullable = false)
    private String clientId;


    @ManyToOne // Relación muchos a uno con User (muchos dispositivos pertenecen a un usuario)
    @JoinColumn(name = "user_id", nullable = false)  // Clave foránea user_id
    private User user;


    @ManyToOne // Relación muchos a uno con Location (muchos dispositivos pertenecen a una ubicación)
    @JoinColumn(name = "location_id", nullable = false)  // Clave foránea location_id
    private Location location;

}
