package ru.moscow.hackathon.coordinator.repository.implementations;

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
public class OdsDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "id_yy");
        FIELDS.put(3, "unom");
        FIELDS.put(4, "building_group");
        FIELDS.put(5, "ods_number");
        FIELDS.put(6, "ods_address");
        FIELDS.put(7, "consumer");
        FIELDS.put(8, "heating_station");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[ODS REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into ods_data(
                                            id,
                                            id_yy,
                                            unom,
                                            building_group,
                                            ods_number,
                                            ods_address,
                                            consumer,
                                            heating_station
                                        ) VALUES (?,?,?,?,?,?,?,?)
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(
                                            PreparedStatement ps,
                                            int i
                                    ) throws SQLException {
                                        var current = it.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id); // id
                                        setString(id, ps, 2, current[0]); // id_yy
                                        setString(id, ps, 3, current[1]); // unom
                                        setString(id, ps, 4, current[2]); // building_group
                                        setString(id, ps, 5, current[3]); // ods_number
                                        setString(id, ps, 6, current[4]); // ods_address
                                        setString(id, ps, 7, current[5]); // consumer
                                        setString(id, ps, 8, current[6]); // heating_station
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
        return OperationType.ODS_DATA;
    }
}
