package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.WeatherDTO;
import ru.moscow.hackathon.coordinator.dto.weather.ArchiveWeatherDTO;
import ru.moscow.hackathon.coordinator.dto.weather.WeatherRoot;
import ru.moscow.hackathon.coordinator.mapping.WeatherMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    public List<Pair<UUID, Double>> onDate(List<Pair<UUID, String>> date) {

        var inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        var dates = date.stream()
                .map(Pair::getSecond)
                .map(a -> LocalDate.parse(a, inputFormatter))
                .sorted()
                .toList();

        var outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var first = dates.get(0).format(outputFormatter);
        var last = dates.get(date.size() - 1).format(outputFormatter);
        var a = client.get()
                .uri(
                        String.format(
                                "https://archive-api.open-meteo.com/v1/era5?latitude=%f&longitude=%f&start_date=%s&end_date=%s&hourly=temperature_2m",
                                55.44,
                                37.36,
                                first,
                                last
                        )
                ).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ArchiveWeatherDTO.class)
                .block();

        if (a == null) {
            throw new IllegalStateException("HOURLY NULL?");
        }

        var hours = a.getHourly();
        var time = hours.getTime();
        var temp = hours.getTemperature_2m();
        return date.stream()
                .map(pair -> {
                    var formatted = LocalDate.parse(pair.getSecond(), inputFormatter).format(outputFormatter);
                    var counter = -1;
                    for (int i = 0; i < time.size(); i++) {
                        var act = time.get(i);
                        if (act.contains(formatted) && counter == -1) {
                            counter = i;
                        }
                        if (act.equals(String.format("%sT12:00", formatted))) {
                            return Pair.of(
                                    pair.getFirst(),
                                    temp.get(i)
                            );
                        }
                    }
                    return Pair.of(
                            pair.getFirst(),
                            counter == -1 ? -1.0 : temp.get(counter)
                    );
                }).toList();
    }
}
