package ru.moscow.hackathon.coordinator.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum SourceType {
    ASUPR,
    EDC,
    KGH,
    MGI,
    NG
}
