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
public class BtiDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "street");
        FIELDS.put(3, "address_number_type");
        FIELDS.put(4, "address_number");
        FIELDS.put(5, "unom");
        FIELDS.put(6, "exterior_wall_material");
        FIELDS.put(7, "destination");
        FIELDS.put(8, "building_class");
        FIELDS.put(9, "full_square");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[BTI REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into bti_data(
                                            id,
                                            street,
                                            address_number_type,
                                            address_number, 
                                            unom,
                                            exterior_wall_material,
                                            destination,
                                            building_class,
                                            full_square
                                        )
                                         VALUES (?,?,?,?,?,?,?,?,?)
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(
                                            PreparedStatement ps,
                                            int i
                                    ) throws SQLException {
                                        var current = it.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id);
                                        setString(id, ps, 2, current[0]);
                                        setString(id, ps, 3, current[1]);
                                        setString(id, ps, 4, current[2]);
                                        setString(id, ps, 5, current[3]);
                                        setString(id, ps, 6, current[4]);
                                        setString(id, ps, 7, current[5]);
                                        setString(id, ps, 8, current[6]);
                                        setDouble(id, ps, 9, current[7]);
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
        return OperationType.BTI_DATA;
    }
}
