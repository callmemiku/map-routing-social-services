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

    ASUPR_DATA(10, IntStream.rangeClosed(0, 9).boxed().toList()),
    BTI_DATA(20, List.of(11, 12, 13, 14, 15, 16, 19)),
    ODPU_DATA(23, List.of(5, 6, 8, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22));

    Integer rowWidth; //original
    List<Integer> cells; //from 0 to N
}
