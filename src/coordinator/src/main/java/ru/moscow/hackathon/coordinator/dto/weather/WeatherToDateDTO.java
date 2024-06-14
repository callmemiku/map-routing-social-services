package ru.moscow.hackathon.coordinator.dto.weather;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class WeatherToDateDTO {

    String dateYYYY, dateDD;
    Double temperature;

}
