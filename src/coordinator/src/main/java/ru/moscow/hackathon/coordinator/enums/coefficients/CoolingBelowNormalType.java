package ru.moscow.hackathon.coordinator.enums.coefficients;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.EnumSet;
import java.util.function.Predicate;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum CoolingBelowNormalType {

    FAST(1, (v) -> v < 1),
    MEDIUM(2, (v) -> v >= 1 && v <= 3),
    SLOW(3, (v) -> v > 3);

    Integer priority;
    Predicate<Double> amount;

    public static Integer priority(Double amount) {
        if (amount == null) {
            return 3;
        } else {
            return EnumSet.allOf(CoolingBelowNormalType.class)
                    .stream()
                    .filter(it -> it.amount.test(amount))
                    .map(it -> it.priority)
                    .findFirst()
                    .orElse(3);
        }
    }
}
