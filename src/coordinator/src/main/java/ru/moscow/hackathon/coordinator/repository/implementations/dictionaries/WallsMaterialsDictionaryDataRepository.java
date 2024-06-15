package ru.moscow.hackathon.coordinator.repository.implementations.dictionaries;

import jakarta.annotation.PostConstruct;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WallsMaterialsDictionaryDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;
    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
                FIELDS.put(2,"typical_series");
                FIELDS.put(3,"exterior_wall_material");
                FIELDS.put(4,"thickness");
                FIELDS.put(5,"outer_layer");
                FIELDS.put(6,"insulation_layer");
                FIELDS.put(7,"inner_layer");
                FIELDS.put(8,"calc_heat_transfer_resistance");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[WALLS MATERIALS DICT REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        INSERT INTO walls_materials_dictionary (
                                            id, 
                                            typical_series, 
                                            exterior_wall_material, 
                                            thickness, 
                                            outer_layer, 
                                            insulation_layer, 
                                            inner_layer, 
                                            calc_heat_transfer_resistance
                                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
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
                                        setDouble(id, ps, 4, current[2]);
                                        setDouble(id, ps, 5, current[3]);
                                        setDouble(id, ps, 6, current[4]);
                                        setDouble(id, ps, 7, current[5]);
                                        setDouble(id, ps, 8, current[6]);
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
    protected Map<Integer, String> errors() {
        return FIELDS;
    }

    @Override
    public OperationType myType() {
        return OperationType.WALLS_DICTIONARY;
    }
}
