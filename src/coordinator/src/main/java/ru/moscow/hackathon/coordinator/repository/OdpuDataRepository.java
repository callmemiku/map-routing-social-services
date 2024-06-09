package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.enums.CustomSchedulers;
import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OdpuDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(List<String[]> rows) {
        log.debug("[BTI REPO] saving {} records", rows.size());
        Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                "insert into bti_data(id, unom, unad, material, building_usage_type, building_class, building_type, square) VALUES (?,?,?,?,?,?,?,?)",
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(
                                            PreparedStatement ps,
                                            int i
                                    ) throws SQLException {
                                        var current = it.get(i);
                                        ps.setObject(1, UUID.randomUUID());
                                        ps.setLong(2, Long.parseLong(current[0]));
                                        ps.setLong(3, Long.parseLong(current[1]));
                                        ps.setString(4, current[2]);
                                        ps.setString(5, current[3]);
                                        ps.setString(6, current[4]);
                                        ps.setString(7, current[5]);
                                        var square = Optional.ofNullable(current[6])
                                                .map(it -> it.replace(",", "."))
                                                .map(Double::parseDouble)
                                                .orElse(null);
                                        if (square == null) {
                                            ps.setNull(8, Types.NUMERIC);
                                        } else {
                                            ps.setDouble(8, square);
                                        }

                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return it.size();
                                    }
                                }
                        )
                ).subscribeOn(
                CustomSchedulers.DB_BLOCKING.getScheduler()
        ).subscribe();
    }

    @Override
    public OperationType myType() {
        return OperationType.ODPU_DATA;
    }
}
