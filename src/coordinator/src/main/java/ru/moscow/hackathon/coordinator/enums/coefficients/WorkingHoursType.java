package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.sql.In;

import java.util.EnumSet;
import java.util.Locale;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum WorkingHoursType {

    THREE(3, "9:00-18:00"),
    TWO(2, "9:00-21:00"),
    ONE(1, "КРУГЛОСУТОЧНО");

    Integer priority;
    String workingHours;

    public static Integer priority(String type) {
        if (type == null) {
            return -1;
        } else {
            return EnumSet.allOf(WorkingHoursType.class)
                    .stream()
                    .filter(it -> it.workingHours.equals(
                            type.toUpperCase(Locale.ROOT).replaceAll(" ", ""))
                    ).map(it -> it.priority)
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalArgumentException("Неизвестный тип потребителя: " + type)
                    );
        }
    }

    public static Integer priority(Integer group) {
        if (group == null) {
            return -1;
        } else {
            return switch (group) {
                case 3 -> 1;
                case 2 -> 2;
                case 1 -> 3;
                default -> 1;
            };
        }
    }
}
