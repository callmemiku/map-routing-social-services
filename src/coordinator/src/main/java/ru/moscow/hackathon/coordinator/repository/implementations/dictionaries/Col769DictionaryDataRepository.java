package ru.moscow.hackathon.coordinator.repository.implementations.dictionaries;

import jakarta.annotation.PostConstruct;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Col769DictionaryDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "code");
        FIELDS.put(3, "description");
    }

    @Override
    protected Map<Integer, String> errors() {
        return FIELDS;
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[COL769 DICT REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        INSERT INTO col_769_data (
                                            id, 
                                            code,
                                            description
                                        ) VALUES (?, ?, ?)
                                    """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(
                                            PreparedStatement ps,
                                            int i
                                    ) throws SQLException {
                                        String[] current = rows.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id);
                                        setString(id, ps, 2, current[0]);
                                        setString(id, ps, 3, current[1]);
                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return rows.size();
                                    }
                                }
                        )
                ).map(it -> Collections.emptyList());
    }

    @Override
    public OperationType myType() {
        return OperationType.COL_769_DICTIONARY;
    }
}
