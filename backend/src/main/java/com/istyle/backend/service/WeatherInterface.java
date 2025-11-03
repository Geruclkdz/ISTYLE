package com.istyle.backend.service;

import java.util.Map;

public interface WeatherInterface {
    Map<String, Object> getWeather(String location);

}
