package ru.moscow.hackathon.coordinator.repository.implementations;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.repository.CoordinatedRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PowerEfficiencyDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[POWER EFFICIENCY REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into power_efficiency_data(
                                            id,
                                            building,
                                            full_square,
                                            full_square_heated,
                                            employees_number,
                                            building_type,
                                            energy_class,
                                            building_wear,
                                            commissioning_year
                                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        var current = it.get(i);
                                        ps.setObject(1, UUID.randomUUID()); // id
                                        ps.setString(2, current[0]);
                                        setDouble(ps, 3, current[1]);
                                        setDouble(ps, 4, current[2]);
                                        setInt(ps, 5, current[3]);
                                        ps.setString(6, current[4]);
                                        ps.setString(7, current[5]);
                                        setDouble(ps, 8, current[6]);
                                        ps.setString(9, current[7]);
                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return it.size();
                                    }
                                }
                        )
                ).map(it -> Collections.emptyList());
    }

    @Override
    public OperationType myType() {
        return OperationType.POWER_EFFICIENCY_DATA;
    }
}
