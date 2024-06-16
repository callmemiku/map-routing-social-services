package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum EfficiencyType {
    THREE(3, List.of("A", "A+", "A++")),
    TWO(2, List.of("B")),
    ONE(1, List.of("C", "D", "E", "F", "G"));

    Integer priority;
    List<String> types;

    public static Integer priority(String type) {
        if (type == null) {
            return 1;
        } else {
            return EnumSet.allOf(EfficiencyType.class)
                    .stream()
                    .filter(it -> it.types.contains(type.toUpperCase(Locale.ROOT)))
                    .map(it -> it.priority)
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalArgumentException("Неизвестный тип потребителя: " + type)
                    );
        }
    }
}
