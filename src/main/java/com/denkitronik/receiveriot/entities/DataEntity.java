package com.denkitronik.receiveriot.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * DataEntity que representa los datos de las mediciones de los dispositivos
 */
@Getter
@Setter
@Entity
@Table(name = "data")
public class DataEntity {

    @Id
    @Column(name = "unix_time", nullable = false)
    private Long unix_time;
    private float variable_value;
    private ZonedDateTime base_time;

    @ManyToOne
    @JoinColumn(name = "variable_id")
    private Measurement variable;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @PrePersist
    public void prePersist() {
        this.unix_time = System.currentTimeMillis();
        ZoneId zoneId = ZoneId.of("America/Bogota");
        this.setBase_time(ZonedDateTime.now(zoneId));
    }
}

