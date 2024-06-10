package ru.moscow.hackathon.coordinator.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.stream.IntStream;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public enum OperationType {

    ASUPR_DATA(
            10,
            IntStream.rangeClosed(0, 9).boxed().toList()
    ),
    BTI_DATA(
            20,
            List.of(5, 6, 7, 11, 13, 14, 15, 19)
    ),
    ODPU_DATA(
            23,
            List.of(4, 5, 6, 7, 10, 12, 14, 15, 17, 18, 19, 20, 21, 22)
    ),
    EVENTS(
            8,
            IntStream.rangeClosed(0, 7).boxed().toList()
    ),
    ODS_DATA(
            10,
            List.of(0, 4, 5, 6, 7, 8, 9)
    ),
    MOEK_DATA(
            16,
            List.of(1, 3, 5, 6, 10, 12, 13)
    ),
    PERFORMANCE_DATA(
            22,
            List.of(1, 9, 10, 13, 18)
    ),
    WALLS_MATERIALS_DATA(
            6,
            IntStream.rangeClosed(0, 5).boxed().toList()
    ),
    ADDRESS_REGISTRY_DATA(
            44,
            List.of(4, 42, 43)
    ),
    DISCONNECTIONS_DATA(
            10,
            List.of(5, 8, 9)
    ),
    POWER_EFFICIENCY_DATA(
            12,
            List.of(0, 2, 3, 4, 6, 9, 11)
    );

    Integer rowWidth; //original
    List<Integer> cells; //from 0 to N
}
