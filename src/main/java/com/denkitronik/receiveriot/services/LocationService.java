package com.denkitronik.receiveriot.services;

import com.denkitronik.receiveriot.entities.Location;
import com.denkitronik.receiveriot.repositories.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final RestTemplate restTemplate;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
        this.restTemplate = new RestTemplate();
    }

    public Location getOrCreateLocation(String city, String state, String country) {
        return locationRepository.findByCityAndStateAndCountry(city, state, country).orElseGet(() -> {
            Location newLocation = new Location();
            newLocation.setCity(city);
            newLocation.setState(state);
            newLocation.setCountry(country);

            Map<String, Double> coordinates = getCoordinatesFromApi(city, state, country);
            newLocation.setLatitude(coordinates.get("latitude"));
            newLocation.setLongitude(coordinates.get("longitude"));
            return locationRepository.save(newLocation);
        });
    }

    private Map<String, Double> getCoordinatesFromApi(String city, String state, String country) {
        // Construir la URL para la API de geocode.xyz
        String url = UriComponentsBuilder.fromHttpUrl("https://geocode.xyz")
                .pathSegment(city, state, country)
                .queryParam("json", "1")
                .toUriString();

        // Realizar la solicitud GET a la API y obtener la respuesta en un Map
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null) {
            // Extraer latitud y longitud del Map
            Double latitude = Double.parseDouble((String) response.get("latt"));
            Double longitude = Double.parseDouble((String) response.get("longt"));

            // Crear un nuevo mapa con las coordenadas
            return Map.of("latitude", latitude, "longitude", longitude);
        }

        // Lanzar una excepción si no se pudo obtener la respuesta
        throw new RuntimeException("No se pudo obtener las coordenadas para la ciudad: " + city);
    }

}

