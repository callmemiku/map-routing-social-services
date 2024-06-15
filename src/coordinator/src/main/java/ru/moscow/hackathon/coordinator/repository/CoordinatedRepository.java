package ru.moscow.hackathon.coordinator.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class CoordinatedRepository {

    public abstract Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows);

    public abstract OperationType myType();

    protected abstract Map<Integer, String> errors();

    public Mono<Void> doAfter(List<Pair<UUID, List<String>>> rows) {
        return Mono.empty();
    }

    protected void setDouble(
            UUID id,
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
            processNullField(position, id);
            ps.setNull(position, Types.NUMERIC);
        } else {
            ps.setDouble(position, numeric);
        }
    }

    protected void setLong(
            UUID id,
            PreparedStatement ps,
            Integer position,
            String source
    ) throws SQLException {
        var number = Optional.ofNullable(source)
                .map(it -> it.replace(",", ""))
                .map(Long::parseLong)
                .orElse(null);
        if (number == null) {
            processNullField(position, id);
            ps.setNull(position, Types.BIGINT);
        } else {
            ps.setLong(position, number);
        }
    }

    protected void setInt(
            UUID id,
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
            processNullField(position, id);
            ps.setNull(position, Types.INTEGER);
        } else {
            ps.setLong(position, number);
        }
    }

    protected void setString(
            UUID id,
            PreparedStatement ps,
            Integer position,
            String source
    ) throws SQLException {
        if (source == null) {
            processNullField(position, id);
            ps.setNull(position, Types.INTEGER);
        } else {
            if (errors().get(position).toUpperCase(Locale.ROOT).equals("UNOM"))
                ps.setString(position, source.split("\\.")[0]);
            else ps.setString(position, source);
        }
    }


    private void processNullField(Integer position, UUID id) {

        var field = errors().get(position);

        log.error(
                "[{}] Нет значения для поля {} для записи {}", myType(), field, id
        );
    }
}
