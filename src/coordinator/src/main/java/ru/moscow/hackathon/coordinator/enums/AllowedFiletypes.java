package ru.moscow.hackathon.coordinator.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum AllowedFiletypes {
    XLSX("xlsx"),
    CSV("csv");
    String value;

    public String value() {
        return value;
    }
}
