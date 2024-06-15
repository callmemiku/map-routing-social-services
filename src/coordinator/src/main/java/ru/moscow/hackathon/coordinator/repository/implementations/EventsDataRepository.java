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
public class EventsDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "description");
        FIELDS.put(3, "system");
        FIELDS.put(4, "external_created");
        FIELDS.put(5, "completed");
        FIELDS.put(6, "region_name");
        FIELDS.put(7, "unom");
        FIELDS.put(8, "address");
        FIELDS.put(9, "external_completed");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[EVENTS REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into events(
                                            id,
                                            description,
                                            system,
                                            external_created,
                                            completed,
                                            region_name,
                                            unom,
                                            address,
                                            external_completed
                                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        var current = it.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id); // id
                                        setString(id, ps, 2, current[0]); // description
                                        setString(id, ps, 3, current[1]); // system
                                        setString(id, ps, 4, current[2]); // external_created
                                        setString(id, ps, 5, current[3]); // completed
                                        setString(id, ps, 6, current[4]); // region_name
                                        setString(id, ps, 7, current[5]); // unom
                                        setString(id, ps, 8, current[6]); // address
                                        setString(id, ps, 9, current[7]); // external_completed
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
        return OperationType.EVENTS;
    }
}
