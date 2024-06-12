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
public class AsuprDataRepository implements CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    @Override
    public void handle(OperationType type, List<String[]> rows) {
        log.debug("[ASUPR REPO] saving {} records", rows.size());
        Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                "insert into asupr_data(id, id_uu, address, address_full, region, unom, building_group, ods_identification, ods_address, consumer, warm_point_id) values (?,?,?,?,?,?,?,?,?,?,?)",
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(
                                            PreparedStatement ps,
                                            int i
                                    ) throws SQLException {
                                        var currRow = it.get(i);
                                        ps.setObject(1, UUID.randomUUID());
                                        ps.setLong(2, Long.parseLong(currRow[0]));
                                        ps.setString(3, currRow[1]);
                                        ps.setString(4, currRow[2]);
                                        ps.setString(5, currRow[3]);
                                        ps.setLong(6, Long.parseLong(currRow[4]));
                                        ps.setString(7, currRow[5]);
                                        ps.setString(8, currRow[6]);
                                        ps.setString(9, currRow[7]);
                                        ps.setString(10, currRow[8]);
                                        ps.setString(11, currRow[9]);
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
        return OperationType.ASUPR_DATA;
    }
}
