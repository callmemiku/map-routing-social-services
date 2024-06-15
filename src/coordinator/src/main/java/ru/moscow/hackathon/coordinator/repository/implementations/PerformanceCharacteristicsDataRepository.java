package ru.moscow.hackathon.coordinator.repository.implementations;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import org.springframework.data.util.Pair;

import java.util.Collections;

import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.repository.CoordinatedRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class PerformanceCharacteristicsDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "address");
        FIELDS.put(3, "unom");
        FIELDS.put(4, "seria");
        FIELDS.put(5, "full_square");
        FIELDS.put(6, "full_live_square");
        FIELDS.put(7, "exterior_wall_material");
        FIELDS.put(8, "accident_rate");
        FIELDS.put(9, "roof_material");
        FIELDS.put(10, "fond_type");
        FIELDS.put(11, "status_mkd");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[PERF CHARS REPO] saving {} records", rows.size());
        return Mono.just(rows)
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
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id); // id
                                        setString(id, ps, 2, current[0]); // unom
                                        setString(id, ps, 3, current[1]); // exterior_wall_material
                                        setString(id, ps, 4, current[2]); // roof_material
                                        setDouble(id, ps, 5, current[3]); // full_square
                                        setDouble(id, ps, 6, current[4]); // full_live_square
                                        setString(id, ps, 7, current[5]);
                                        setString(id, ps, 8, current[6]);
                                        setString(id, ps, 9, current[7]);
                                        setString(id, ps, 10, current[8]);
                                        setString(id, ps, 11, current[9]);

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
    protected Map<Integer, String> errors() {
        return FIELDS;
    }

    @Override
    public OperationType myType() {
        return OperationType.PERFORMANCE_DATA;
    }
}
