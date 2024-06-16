package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.relational.core.sql.In;

import java.util.EnumSet;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum TemperatureByGroup {

    SOCIAL(1, 20),
    INDUSTRIAL(2, 16),
    HOUSE(3, 18);

    Integer group;
    Integer temp;

    public static Integer byGroup(Integer group) {
        if (group == null) {
            return 18;
        } else {
            return EnumSet.allOf(TemperatureByGroup.class)
                    .stream()
                    .filter(it -> it.group.equals(group))
                    .map(it -> it.group)
                    .findFirst()
                    .orElse(18);
        }
    }
}
