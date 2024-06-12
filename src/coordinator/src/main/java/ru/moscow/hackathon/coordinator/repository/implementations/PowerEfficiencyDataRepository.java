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
public class PowerEfficiencyDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[POWER EFFICIENCY REPO] saving {} records", rows.size());
        Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                    insert into power_efficiency_data(
                                        id,
                                        building,
                                        energy_class,
                                        full_square,
                                        employees_number,
                                        building_type,
                                        building_wear,
                                        commissioning_year
                                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
                                    """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        var current = it.get(i);
                                        ps.setObject(1, UUID.randomUUID()); // id
                                        ps.setString(2, current[0]); // building
                                        ps.setString(3, current[1]); // energy_class
                                        setDouble(ps, 4, current[2]); // full_square
                                        setInt(ps, 5, current[3]); // employees_number
                                        ps.setString(6, current[4]); // building_type
                                        setDouble(ps, 7, current[5]); // building_wear
                                        ps.setString(8, current[6]); // commissioning_year
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
        return OperationType.POWER_EFFICIENCY_DATA;
    }
}
