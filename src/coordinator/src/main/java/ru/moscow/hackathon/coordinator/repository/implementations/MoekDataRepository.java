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
public class MoekDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "heating_station_number");
        FIELDS.put(3, "heating_station_type");
        FIELDS.put(4, "heat_source");
        FIELDS.put(5, "city_district");
        FIELDS.put(6, "address");
        FIELDS.put(7, "thermal_load_hws");
        FIELDS.put(8, "thermal_load_building");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[MOEK REPO] saving {} records", rows.size());
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                        insert into moek_connection_data(
                                            id,
                                            heating_station_number,
                                            heating_station_type,
                                            heat_source,
                                            city_district,
                                            address,
                                            thermal_load_hws,
                                            thermal_load_building
                                        ) VALUES (?,?,?,?,?,?,?,?)
                                        """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        var current = rows.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id); // id
                                        setString(id, ps, 2, current[0]); // heating_station_number
                                        setString(id, ps, 3, current[1]); // heating_station_type
                                        setString(id, ps, 4, current[2]); // heat_source
                                        setString(id, ps, 5, current[3]); // city_district
                                        setString(id, ps, 6, current[4]); // address
                                        setDouble(id, ps, 7, current[5]); // thermal_load_hws
                                        setDouble(id, ps, 8, current[6]); // thermal_load_building
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
        return OperationType.MOEK_DATA;
    }
}
