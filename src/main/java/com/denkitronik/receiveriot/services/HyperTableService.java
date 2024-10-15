package com.denkitronik.receiveriot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

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
            logger.error("Hypertable 'data_entity' creada o ya existente.");
        } catch (Exception e) {
            // Manejar cualquier error durante la ejecución del SQL
            logger.error("Error al crear la hypertable: {}", e.getMessage());
        }
    }
}

