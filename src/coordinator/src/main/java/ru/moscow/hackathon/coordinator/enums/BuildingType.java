package ru.moscow.hackathon.coordinator.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum BuildingType {

    SOCIAL(List.of("СОЦИАЛЬНЫЙ", "БЮДЖЕТ"), 1),
    INDUSTRIAL(List.of("ПРОМЫШЛЕННЫЙ"), 2),
    HOUSE(List.of("МКД"), 3);

    List<String> type;
    Integer priority;

    public static Integer priority(String type) {
        if (type == null) {
            return -1;
        } else {
            return EnumSet.allOf(BuildingType.class)
                    .stream()
                    .filter(it -> it.type.contains(type.toUpperCase(Locale.ROOT)))
                    .map(it -> it.priority)
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalArgumentException("Неизвестный тип потребителя: " + type)
                    );
        }
    }
}
