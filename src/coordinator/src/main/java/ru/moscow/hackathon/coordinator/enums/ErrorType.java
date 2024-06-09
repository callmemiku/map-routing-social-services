package ru.moscow.hackathon.coordinator.enums;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public enum ErrorType {

    U("Отсутствие электропитания"),
    D("Разница температур в подающем и обратном трубопроводах меньше минимального"),
    g("Расход меньше минимального"),
    G("Расход больше максимального"),
    E("Функциональный отказ");
    String description;
}
