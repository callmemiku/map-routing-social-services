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
public class OdpuDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[ODPU REPO] saving {} records", rows.size());
        Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into odpu_data(
                                                              id,
                                                              consumer,
                                                              building_group,
                                                              unom,
                                                              address,
                                                              heat_counter_number,
                                                              measurement_date,
                                                              heating_volume_in,
                                                              heating_volume_out,
                                                              heat_leakage,
                                                              supply_water_temp,
                                                              return_water_temp,
                                                              heat_counter_hours,
                                                              energy_consumption,
                                                              heat_counter_error
                                                      )
                                                           values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(
                                            PreparedStatement ps,
                                            int i
                                    ) throws SQLException {
                                        //14
                                        var current = it.get(i);
                                        ps.setObject(1, UUID.randomUUID());
                                        ps.setString(2, current[0]);
                                        ps.setString(3, current[1]);
                                        ps.setString(4, current[2]);
                                        ps.setString(5, current[3]);
                                        ps.setString(6, current[4]);
                                        ps.setString(7, current[5]);
                                        setDouble(ps, 8, current[6]);
                                        setDouble(ps, 9, current[7]);
                                        setDouble(ps, 10, current[8]);
                                        setDouble(ps, 11, current[9]);
                                        setDouble(ps, 12, current[10]);
                                        setDouble(ps, 13, current[11]);
                                        setDouble(ps, 14, current[12]);
                                        ps.setString(15, current[13]);
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
        return OperationType.ODPU_DATA;
    }
}
