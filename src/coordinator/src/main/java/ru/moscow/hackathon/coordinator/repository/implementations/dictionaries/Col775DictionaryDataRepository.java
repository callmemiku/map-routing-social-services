package ru.moscow.hackathon.coordinator.repository.implementations.dictionaries;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import org.springframework.data.util.Pair; import java.util.Collections;
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
public class Col775DictionaryDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[COL775 DICT REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        INSERT INTO col_775_data (
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
                                        ps.setObject(1, UUID.randomUUID());
                                        ps.setString(2, current[0]);
                                        ps.setString(3, current[1]);
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
        return OperationType.COL_775_DICTIONARY;
    }
}
