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
public class WallsMaterialsDictionaryDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[WALLS MATERIALS DICT REPO] saving {} records", rows.size());
        Mono.just(rows)
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
                                        ps.setObject(1, UUID.randomUUID());
                                        ps.setString(2, current[0]);
                                        ps.setString(3, current[1]);
                                        setDouble(ps, 4, current[2]);
                                        setDouble(ps, 5, current[3]);
                                        setDouble(ps, 6, current[4]);
                                        setDouble(ps, 7, current[5]);
                                        setDouble(ps, 8, current[6]);
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
        return OperationType.WALLS_DICTIONARY;
    }
}
