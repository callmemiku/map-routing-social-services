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
public class WallsMaterialsDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "typical_series");
        FIELDS.put(3, "exterior_wall_material");
        FIELDS.put(4, "thickness_mm");
        FIELDS.put(5, "outer_layer_insulation");
        FIELDS.put(6, "inner_layer_insulation");
        FIELDS.put(7, "calc_heat_transfer_resistance");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[WALLS MATERIALS REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into walls_materials_data(
                                            id,
                                            typical_series,
                                            exterior_wall_material,
                                            thickness_mm,
                                            outer_layer_insulation,
                                            inner_layer_insulation,
                                            calc_heat_transfer_resistance
                                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        var current = it.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id); // id
                                        setString(id, ps,2, current[0]); // typical_series
                                        setString(id, ps,3, current[1]); // exterior_wall_material
                                        setInt(id, ps, 4, current[2]); // thickness_mm
                                        setDouble(id, ps, 5, current[3]); // outer_layer_insulation
                                        setDouble(id, ps, 6, current[4]); // inner_layer_insulation
                                        setDouble(id, ps, 7, current[5]); // calc_heat_transfer_resistance
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
        return OperationType.WALLS_MATERIALS_DATA;
    }
}
