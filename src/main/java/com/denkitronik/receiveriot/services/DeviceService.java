package com.denkitronik.receiveriot.services;

import com.denkitronik.receiveriot.entities.Location;
import com.denkitronik.receiveriot.entities.Device;
import com.denkitronik.receiveriot.repositories.DeviceRepository;
import com.denkitronik.receiveriot.entities.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device getOrCreateDevice(String clientId, User user, Location location) {
        // Busca si ya existe un dispositivo con el clientId y la ubicación dadas
        Optional<Device> existingDevice = deviceRepository.findByClientIdAndLocation(clientId, location);

        return existingDevice.orElseGet(() -> {
            // Crear un nuevo dispositivo si no existe
            Device newDevice = new Device();
            newDevice.setClientId(clientId);
            newDevice.setUser(user);
            newDevice.setLocation(location);
            return deviceRepository.save(newDevice);
        });
    }
}
