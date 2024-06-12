package ru.moscow.hackathon.coordinator.repository.implementations.dictionaries;

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
public class Col769DictionaryDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[COL769 DICT REPO] saving {} records", rows.size());
        Mono.just(rows)
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
                ).subscribeOn(CustomSchedulers.DB_BLOCKING.getScheduler())
                .subscribe();
    }

    @Override
    public OperationType myType() {
        return OperationType.COL_769_DICTIONARY;
    }
}
