package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.WeatherDTO;
import ru.moscow.hackathon.coordinator.dto.weather.WeatherRoot;
import ru.moscow.hackathon.coordinator.mapping.WeatherMapper;

import java.util.Map;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WeatherGathererService {

    WebClient client;
    WeatherMapper mapper;

    public Mono<WeatherDTO> getCurrentWeather() {
        return client.get()
                .uri(
                        "https://api.open-meteo.com/v1/forecast",
                        Map.of(
                                "latitude", 55.44,
                                "longitude", 37.36,
                                "current", "temperature_2m,wind_speed_10m,relative_humidity_2m"
                        )
                ).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(WeatherRoot.class)
                .map(mapper::map);
    }
}
