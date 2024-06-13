package ru.moscow.hackathon.coordinator.dto.weather;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ArchiveWeatherDTO {

    Hourly hourly;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class Hourly {
        List<String> time;
        List<Double> temperature_2m;
    }
}
