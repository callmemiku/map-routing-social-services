package ru.moscow.hackathon.coordinator.mapping;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.moscow.hackathon.coordinator.dto.WeatherDTO;
import ru.moscow.hackathon.coordinator.dto.weather.WeatherRoot;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WeatherMapper {

        public WeatherDTO map(WeatherRoot root) {

        var current = root.getCurrent();

        return new WeatherDTO(
                current.getTemperature_2m(),
                current.getRelative_humidity_2m(),
                current.getWind_speed_10m()
        );
    }
}
