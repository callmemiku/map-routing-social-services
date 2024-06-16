package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.function.Predicate;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum EmployeeCountType {

    BIG(1, (v) -> v > 1000),
    MID(2, (v) -> v <= 1000 && v >= 100),
    SMALL(3, (v) -> v < 100);

    Integer priority;
    Predicate<Integer> amount;

    public static Integer priority(Integer amount) {
        if (amount == null) {
            return 3;
        } else {
            return EnumSet.allOf(EmployeeCountType.class)
                    .stream()
                    .filter(it -> it.amount.test(amount))
                    .map(it -> it.priority)
                    .findFirst()
                    .orElse(3);
        }
    }
}
