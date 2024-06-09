package ru.moscow.hackathon.coordinator.dto.weather;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class WeatherRoot {
    double latitude;
    double longitude;
    double generationtime_ms;
    int utc_offset_seconds;
    String timezone;
    String timezone_abbreviation;
    int elevation;
    CurrentUnits current_units;
    Current current;

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class CurrentUnits {
        String time;
        String interval;
        String temperature_2m;
        String wind_speed_10m;
        String relative_humidity_2m;
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Data
    public static class Current {
        String time;
        int interval;
        double temperature_2m;
        double wind_speed_10m;
        int relative_humidity_2m;
    }
}



