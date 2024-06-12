package ru.moscow.hackathon.coordinator.repository.implementations;

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
import ru.moscow.hackathon.coordinator.repository.CoordinatedRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PerformanceCharacteristicsDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[PERF CHARS REPO] saving {} records", rows.size());
        Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                    insert into performance_characteristics_data(
                                        id,
                                        address,
                                        unom,
                                        seria,
                                        full_square,
                                        full_live_square,
                                        exterior_wall_material,
                                        accident_rate,
                                        roof_material,
                                        fond_type,
                                        status_mkd
                                    ) values (?,?,?,?,?,?,?,?,?,?,?)
                                    """,//11
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        var current = it.get(i);
                                        ps.setObject(1, UUID.randomUUID()); // id
                                        ps.setString(2, current[0]); // unom
                                        ps.setString(3, current[1]); // exterior_wall_material
                                        ps.setString(4, current[2]); // roof_material
                                        setDouble(ps, 5, current[3]); // full_square
                                        setDouble(ps, 6, current[4]); // full_live_square
                                        ps.setString(7, current[5]);
                                        ps.setString(8, current[6]);
                                        ps.setString(9, current[7]);
                                        ps.setString(10, current[8]);
                                        ps.setString(11, current[9]);

                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return it.size();
                                    }
                                }
                        )
                ).subscribeOn(CustomSchedulers.DB_BLOCKING.getScheduler())
                .subscribe();
    }

    @Override
    public OperationType myType() {
        return OperationType.PERFORMANCE_DATA;
    }
}
