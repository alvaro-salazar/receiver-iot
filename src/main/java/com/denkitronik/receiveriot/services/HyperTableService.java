package com.denkitronik.receiveriot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Clase que se encarga de crear la hypertable en la base de datos TimescaleDB
 * si no existe.
 */
@Service
public class HyperTableService {

    private static final Logger logger = LoggerFactory.getLogger(HyperTableService.class);

    private final JdbcTemplate jdbcTemplate;

    public HyperTableService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void createHypertableIfNotExists() {
        try {
            // Ejecutar la consulta SQL para crear la hypertable si no existe
            String sql = "SELECT create_hypertable('data', 'unix_time', if_not_exists => TRUE, chunk_time_interval => 86400000);";
            jdbcTemplate.execute(sql);
            logger.info("Hypertable 'data_entity' creada.");
        } catch (Exception e) {
            // Manejar cualquier error durante la ejecución del SQL
            logger.info("La hypertable ya existia: {}", e.getMessage());
        }
    }
}

