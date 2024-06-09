package ru.moscow.hackathon.coordinator.dto;

public record WeatherDTO(
        Double airNow,
        Integer humidity,
        Double windSpeed
) {
}
