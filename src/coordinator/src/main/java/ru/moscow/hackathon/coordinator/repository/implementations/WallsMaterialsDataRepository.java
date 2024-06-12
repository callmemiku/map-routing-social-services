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
public class WallsMaterialsDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[WALLS MATERIALS REPO] saving {} records", rows.size());
        Mono.just(rows)
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
                                        ps.setObject(1, UUID.randomUUID()); // id
                                        ps.setString(2, current[0]); // typical_series
                                        ps.setString(3, current[1]); // exterior_wall_material
                                        setInt(ps, 4, current[2]); // thickness_mm
                                        setDouble(ps, 5, current[3]); // outer_layer_insulation
                                        setDouble(ps, 6, current[4]); // inner_layer_insulation
                                        setDouble(ps, 7, current[5]); // calc_heat_transfer_resistance
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
        return OperationType.WALLS_MATERIALS_DATA;
    }
}
