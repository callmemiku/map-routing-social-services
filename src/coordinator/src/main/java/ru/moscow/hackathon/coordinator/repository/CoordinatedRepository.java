package ru.moscow.hackathon.coordinator.repository;

import org.springframework.data.util.Pair;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoordinatedRepository {

    Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows);

    OperationType myType();

    default Mono<Void> doAfter(List<Pair<UUID, List<String>>> rows) {
        return Mono.empty();
    }

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

}
