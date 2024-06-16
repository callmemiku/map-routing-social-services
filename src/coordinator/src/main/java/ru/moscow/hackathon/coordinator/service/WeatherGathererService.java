package ru.moscow.hackathon.coordinator.service;

import jakarta.annotation.PostConstruct;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WeatherGathererService {

    WebClient client;
    WeatherMapper mapper;
    Map<String, Double> weathers = new ConcurrentHashMap<>();
    WeatherService service;

    @PostConstruct
    public void init() {
        weathers.putAll(service.weathers());
    }

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

    //yyyy-MM-dd
    public Double weatherOnDate(LocalDate date) {
        var outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        var formatted = date.format(outputFormatter);
        if (weathers.containsKey(formatted)) {
            return weathers.get(formatted);
        } else {
            var a = makeApiCall(formatted, formatted);
            if (a == null) {
                throw new IllegalStateException("Weather API can't be polled.");
            }
            var temps = a.getHourly().getTemperature_2m();
            var sum = temps.stream()
                    .mapToDouble(it -> it)
                    .sum() / temps.size();
            weathers.put(formatted, sum);
            return sum;
        }
    }

    public List<Pair<UUID, Double>> onDate(List<Pair<UUID, String>> date) {
        var inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        var outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        var dates = date.stream()
                .map(a -> Pair.of(
                        a.getFirst(),
                        LocalDate.parse(a.getSecond(), inputFormatter))
                ).filter(it -> !weathers.containsKey(it.getSecond().format(outputFormatter)))
                .toList();

        var result = date.stream()
                .map(pair -> {
                            var formatted = LocalDate.parse(pair.getSecond(), inputFormatter).format(outputFormatter);
                            if (weathers.containsKey(formatted)) {
                                return Pair.of(pair.getFirst(), weathers.get(formatted));
                            } else return null;
                        }
                ).filter(Objects::nonNull);

        if (dates.isEmpty()) {
            return result.toList();
        } else {
            var sorted = dates.stream().map(Pair::getSecond).sorted().toList();
            var first = sorted.get(0).format(outputFormatter);
            var last = sorted.get(date.size() - 1).format(outputFormatter);
            var a = makeApiCall(first, last);
            if (a == null) {
                throw new IllegalStateException("HOURLY NULL?");
            }
            var hours = a.getHourly();
            var time = hours.getTime();
            var temp = hours.getTemperature_2m();
            var rest = dates.stream()
                    .map(pair -> {
                        var formatted = pair.getSecond().format(outputFormatter);
                        if (weathers.containsKey(formatted)) {
                            return Pair.of(pair.getFirst(), weathers.get(formatted));
                        } else {
                            var cursors = new ArrayList<Integer>();
                            for (int i = 0; i < time.size(); i++) {
                                var act = time.get(i);
                                if (act.contains(formatted)) {
                                    cursors.add(i);
                                }
                            }
                            var sum = cursors.stream()
                                    .map(temp::get)
                                    .mapToDouble(it -> it)
                                    .sum() / cursors.size();
                            weathers.put(formatted, sum);
                            return Pair.of(
                                    pair.getFirst(),
                                    sum
                            );
                        }
                    });
            service.process(weathers);
            return Stream.concat(result, rest).toList();
        }
    }

    private ArchiveWeatherDTO makeApiCall(String begin, String end) {
        return client.get()
                .uri(
                        String.format(
                                "https://archive-api.open-meteo.com/v1/era5?latitude=%f&longitude=%f&start_date=%s&end_date=%s&hourly=temperature_2m",
                                55.44,
                                37.36,
                                begin,
                                end
                        )
                ).accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ArchiveWeatherDTO.class)
                .block();
    }
}
