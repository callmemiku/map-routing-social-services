package ru.moscow.hackathon.coordinator.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Valid
public class CsvInfo {

    @NotBlank
    String delimiter;

    @NotNull
    int skip;

    @NotNull
    @Size(min = 2, max = 3)
    List<CsvColumn> columns;

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Valid
    public static class CsvColumn {
        @NotNull
        WeatherColumnType column;
        @NotNull
        Integer number;
    }

    public enum WeatherColumnType {
        DATE_YYYY,
        DATE_DD,
        TEMPERATURE
    }
}
