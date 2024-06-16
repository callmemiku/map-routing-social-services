package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.function.Predicate;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum FullCoolingType {

    FAST(1, (v) -> v < 10),
    MEDIUM(2, (v) -> v >= 10 && v <= 20),
    SLOW(3, (v) -> v > 20);

    Integer priority;
    Predicate<Double> amount;

    public static Integer priority(Double amount) {
        if (amount == null) {
            return 3;
        } else {
            return EnumSet.allOf(FullCoolingType.class)
                    .stream()
                    .filter(it -> it.amount.test(amount))
                    .map(it -> it.priority)
                    .findFirst()
                    .orElse(3);
        }
    }
}
