package ru.moscow.hackathon.coordinator.repository;

import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

public interface CoordinatedRepository {

    void handle(OperationType type, List<String[]> rows);
    OperationType myType();

    default void setDouble(
            PreparedStatement ps,
            Integer position,
            String source
    ) throws SQLException {
        var numeric = Optional.ofNullable(source)
                .map(it -> it.replace(",", "."))
                .filter(it -> !it.isEmpty() || !it.isBlank())
                .map(Double::parseDouble)
                .orElse(null);
        if (numeric == null) {
            ps.setNull(position, Types.NUMERIC);
        } else {
            ps.setDouble(position, numeric);
        }
    }

    default void setLong(
            PreparedStatement ps,
            Integer position,
            String source
    ) throws SQLException {
        var number = Optional.ofNullable(source)
                .map(it -> it.replace(",", ""))
                .map(Long::parseLong)
                .orElse(null);
        if (number == null) {
            ps.setNull(position, Types.BIGINT);
        } else {
            ps.setLong(position, number);
        }
    }

    default void setInt(
            PreparedStatement ps,
            Integer position,
            String source
    ) throws SQLException {
        var number = Optional.ofNullable(source)
                .map(it -> it.replace(",", ""))
                .map(it -> it.split("\\.")[0])
                .filter(it -> !it.isEmpty() && !it.isBlank())
                .map(Integer::parseInt)
                .orElse(null);
        if (number == null) {
            ps.setNull(position, Types.INTEGER);
        } else {
            ps.setLong(position, number);
        }
    }

    default void doAfter(List<String[]> rows) {}
}
