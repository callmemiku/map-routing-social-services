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
public class OdsDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[ODS REPO] saving {} records", rows.size());
        Mono.just(rows)
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
                                        ps.setObject(1, UUID.randomUUID()); // id
                                        ps.setString(2, current[0]); // id_yy
                                        ps.setString(3, current[1]); // unom
                                        ps.setString(4, current[2]); // building_group
                                        ps.setString(5, current[3]); // ods_number
                                        ps.setString(6, current[4]); // ods_address
                                        ps.setString(7, current[5]); // consumer
                                        ps.setString(8, current[6]); // heating_station
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
        return OperationType.ODS_DATA;
    }
}
