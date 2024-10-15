package com.denkitronik.receiveriot.services;

import com.denkitronik.receiveriot.entities.Measurement;
import com.denkitronik.receiveriot.repositories.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public Measurement getOrCreateMeasurement(String variable) {
        return measurementRepository.findByName(variable).orElseGet(() -> {
            Measurement newMeasurement = new Measurement();
            newMeasurement.setName(variable);
            newMeasurement.setUnit("default_unit"); // Puedes personalizar la unidad
            return measurementRepository.save(newMeasurement);
        });
    }
}
