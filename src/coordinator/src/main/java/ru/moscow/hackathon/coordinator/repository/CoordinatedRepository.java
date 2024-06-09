package ru.moscow.hackathon.coordinator.repository;

import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.util.List;

public interface CoordinatedRepository {

    void handle(List<String[]> rows);
    OperationType myType();
}
