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
public class EventsDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[EVENTS REPO] saving {} records", rows.size());
        Mono.just(rows)
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
                                        ps.setObject(1, UUID.randomUUID()); // id
                                        ps.setString(2, current[0]); // description
                                        ps.setString(3, current[1]); // system
                                        ps.setString(4, current[2]); // external_created
                                        ps.setString(5, current[3]); // completed
                                        ps.setString(6, current[4]); // region_name
                                        ps.setString(7, current[5]); // unom
                                        ps.setString(8, current[6]); // address
                                        ps.setString(9, current[7]); // external_completed
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
        return OperationType.EVENTS;
    }
}
