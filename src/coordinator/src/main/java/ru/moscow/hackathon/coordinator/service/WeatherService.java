package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.CsvInfo;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.dto.weather.WeatherToDateDTO;
import ru.moscow.hackathon.coordinator.repository.WeatherRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WeatherService {

    WeatherRepository repository;
    Random random = new Random();

    public Mono<StatusDTO> process(
            CsvInfo csvInfo,
            MultipartFile csv
    ) {
        Function<String[], WeatherToDateDTO> action;
        var dates = csvInfo.getColumns().stream().filter(c -> !c.getColumn().equals(CsvInfo.WeatherColumnType.TEMPERATURE)).toList();
        var temp = csvInfo.getColumns().stream().filter(c -> c.getColumn().equals(CsvInfo.WeatherColumnType.TEMPERATURE)).findFirst().orElseThrow(() -> new IllegalArgumentException("Нет колонки с температурами"));
        if (dates.size() == 2) {
            CsvInfo.CsvColumn ddColumn;
            CsvInfo.CsvColumn yyColumn;
            var date = dates.get(0);
            if (date.getColumn().equals(CsvInfo.WeatherColumnType.DATE_DD)) {
                ddColumn = date;
                yyColumn = dates.get(1);
            } else {
                yyColumn = date;
                ddColumn = dates.get(1);
            }
            action = (String[] v) -> {
                var temperature = Optional.ofNullable(v[temp.getNumber()]).map(it -> it.replaceAll(",", ".")).map(Double::parseDouble).orElse(null);
                if (temperature == null) {
                    return null;
                }
                var result = new WeatherToDateDTO();
                result.setDateDD(v[ddColumn.getNumber()]);
                result.setDateYYYY(v[yyColumn.getNumber()]);
                result.setTemperature(temperature);
                return result;
            };
        } else {
            var date = dates.get(0);
            if (date.getColumn().equals(CsvInfo.WeatherColumnType.DATE_DD)) {

                action = (String[] v) -> {
                    var temperature = Optional.ofNullable(v[temp.getNumber()]).map(it -> it.replaceAll(",", ".")).map(Double::parseDouble).orElse(null);
                    if (temperature == null) {
                        return null;
                    }
                    var ddFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    var yyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    var result = new WeatherToDateDTO();
                    var value = v[date.getNumber()];
                    result.setDateDD(value);
                    result.setDateYYYY(LocalDate.parse(value, ddFormatter).format(yyFormatter));
                    result.setTemperature(temperature);
                    return result;
                };


            } else {
                action = (String[] v) -> {
                    var temperature = Optional.ofNullable(v[temp.getNumber()]).map(it -> it.replaceAll(",", ".")).map(Double::parseDouble).orElse(null);
                    if (temperature == null) {
                        return null;
                    }
                    var ddFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    var yyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    var result = new WeatherToDateDTO();
                    var value = v[date.getNumber()];
                    result.setDateYYYY(value);
                    result.setDateDD(LocalDate.parse(value, yyFormatter).format(ddFormatter));
                    result.setTemperature(temperature);
                    return result;
                };

            }
        }

        return Mono.just(
                String.format(
                        "weather-buffer-%s.csv",
                        random.nextLong()
                )
        ).map(File::new)
                .doOnNext(it -> {
                            try {
                                csv.transferTo(it.toPath());
                            } catch (IOException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                ).map(file -> {
                            try {
                                return new BufferedReader(new FileReader(file)).lines();
                            } catch (FileNotFoundException e) {
                                throw new IllegalStateException(e);
                            }
                        }
                ).map(it ->
                        it.skip(csvInfo.getSkip())
                                .map(v -> v.split(csvInfo.getDelimiter()))
                                .map(action)
                                .toList()
                ).doOnNext(repository::saveWeathers)
                .map(a -> new StatusDTO(
                                HttpStatus.OK,
                                "Температуры сохранены",
                                null
                        )
                ).onErrorResume(
                        e -> Mono.just(
                                new StatusDTO(
                                        HttpStatus.BAD_GATEWAY,
                                        "Температуры не сохранены: " + e.getMessage(),
                                        null
                                )
                        )
                );
    }

    public void process(
            Map<String, Double> weatherData
    ) {
        var list = weatherData.entrySet()
                .parallelStream()
                .map(it -> {
                    var outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    var inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    var result = new WeatherToDateDTO();
                    result.setDateYYYY(it.getKey());
                    var reformatted = LocalDate.parse(it.getKey(), inputFormatter).format(outputFormatter);
                    result.setDateDD(reformatted);
                    result.setTemperature(it.getValue());
                    return result;
                }).toList();
        repository.saveWeathers(list);
    }

    public Map<String, Double> weathers() {
        return repository.weathers();
    }
}
