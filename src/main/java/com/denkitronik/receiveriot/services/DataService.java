package com.denkitronik.receiveriot.services;

import com.denkitronik.receiveriot.entities.DataEntity;
import com.denkitronik.receiveriot.repositories.DataRepository;
import com.denkitronik.receiveriot.entities.Measurement;
import com.denkitronik.receiveriot.entities.Device;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class DataService {

    private final DataRepository dataRepository;

    public DataService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void createData(float value, Device device, Measurement variable, ZonedDateTime time) {
        if (device == null || variable == null || time == null) {
            throw new IllegalArgumentException("Device, Measurement, and Timestamp cannot be null");
        }
        DataEntity data = new DataEntity();
        data.setVariableValue(value);
        data.setDevice(device);
        data.setVariable(variable);
        data.setBaseTime(time);
        dataRepository.save(data);
    }
}
