package com.istyle.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherImpl implements WeatherInterface {

    @Value("${application.weather.api.key}")
    private String weatherApiKey;

    @Value("${application.weather.api.url}")
    private String weatherApiUrl;

    @Override
    public Map<String, Object> getWeather(String location) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = weatherApiUrl + "?key=" + weatherApiKey + "&q=" + location;

        // Make the API call
        ResponseEntity<Map> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, null, Map.class);
        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new RuntimeException("Failed to fetch weather data. Status: " + responseEntity.getStatusCode());
        }

        Map<String, Object> response = responseEntity.getBody();
        if (!response.containsKey("current")) {
            throw new RuntimeException("Invalid weather data: 'current' section is missing.");
        }

        // Extract weather data
        Map<String, Object> currentWeather = (Map<String, Object>) response.get("current");
        Map<String, Object> condition = (Map<String, Object>) currentWeather.getOrDefault("condition", new HashMap<>());

        // Populate the required weather details
        Map<String, Object> weatherDetails = new HashMap<>();
        weatherDetails.put("temperature", currentWeather.get("temp_c"));
        weatherDetails.put("precipitation_mm", currentWeather.get("precip_mm")); // Precipitation in millimeters
        weatherDetails.put("condition", condition.get("text")); // Weather condition (e.g., Rain, Mist, Clear)
        weatherDetails.put("wind_speed_kph", currentWeather.get("wind_kph")); // Wind speed in kilometers per hour

        return weatherDetails;
    }
}

