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
public class AsuprDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "id_uu");
        FIELDS.put(3, "address");
        FIELDS.put(4, "address_full");
        FIELDS.put(5, "region");
        FIELDS.put(6, "unom");
        FIELDS.put(7, "building_group");
        FIELDS.put(8, "ods_identification");
        FIELDS.put(9, "ods_address");
        FIELDS.put(10, "consumer");
        FIELDS.put(11, "warm_point_id");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[ASUPR REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into asupr_data(
                                            id, 
                                            id_uu,
                                            address,
                                            address_full, 
                                            region, 
                                            unom, 
                                            building_group, 
                                            ods_identification, 
                                            ods_address, 
                                            consumer, 
                                            warm_point_id
                                        ) values (?,?,?,?,?,?,?,?,?,?,?)
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(
                                            PreparedStatement ps,
                                            int i
                                    ) throws SQLException {
                                        var currRow = it.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id);
                                        setLong(id, ps, 2, currRow[0]);
                                        setString(id, ps,3, currRow[1]);
                                        setString(id, ps,4, currRow[2]);
                                        setString(id, ps,5, currRow[3]);
                                        setLong(id, ps, 6, currRow[4]);
                                        setString(id, ps,7, currRow[5]);
                                        setString(id, ps,8, currRow[6]);
                                        setString(id, ps,9, currRow[7]);
                                        setString(id, ps,10, currRow[8]);
                                        setString(id, ps,11, currRow[9]);
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
        return OperationType.ASUPR_DATA;
    }
}
