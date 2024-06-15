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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.repository.CoordinatedRepository;
import ru.moscow.hackathon.coordinator.service.WeatherGathererService;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class OdpuDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;
    WeatherGathererService weather;

    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2, "consumer");
        FIELDS.put(3, "building_group");
        FIELDS.put(4, "unom");
        FIELDS.put(5, "address");
        FIELDS.put(6, "heat_counter_number");
        FIELDS.put(7, "measurement_date");
        FIELDS.put(8, "heating_volume_in");
        FIELDS.put(9, "heating_volume_out");
        FIELDS.put(10, "heat_leakage");
        FIELDS.put(11, "supply_water_temp");
        FIELDS.put(12, "return_water_temp");
        FIELDS.put(13, "heat_counter_hours");
        FIELDS.put(14, "energy_consumption");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        List<Pair<UUID, List<String>>> storage = new ArrayList<>();
        log.debug("[ODPU REPO] saving {} records", rows.size());
        return Mono.just(rows)
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
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id);
                                        setString(id, ps, 2, current[0]);
                                        setString(id, ps, 3, current[1]);
                                        setString(id, ps, 4, current[2]);
                                        setString(id, ps, 5, current[3]);
                                        setString(id, ps, 6, current[4]);
                                        setString(id, ps, 7, current[5]);
                                        setDouble(id, ps, 8, current[6]);
                                        setDouble(id, ps, 9, current[7]);
                                        setDouble(id, ps, 10, current[8]);
                                        setDouble(id, ps, 11, current[9]);
                                        setDouble(id, ps, 12, current[10]);
                                        setDouble(id, ps, 13, current[11]);
                                        setDouble(id, ps, 14, current[12]);
                                        ps.setString(15, current[13]);
                                        storage.add(Pair.of(id, List.of(current[5])));
                                    }

                                    @Override
                                    public int getBatchSize() {
                                        return it.size();
                                    }
                                }
                        )
                ).map(it -> storage);
    }

    @Override
    public Mono<Void> doAfter(List<Pair<UUID, List<String>>> rows) {
        return Flux.fromIterable(rows)
                .map(it -> Pair.of(it.getFirst(), it.getSecond().get(0)))
                .collectList()
                .map(weather::onDate)
                .doOnNext(it -> {
                            log.debug("[WEATHER] saving weather info for {} records", it.size());
                            jdbcTemplate.batchUpdate(
                                    "update odpu_data set temperature = ? where id = ?",
                                    new BatchPreparedStatementSetter() {
                                        @Override
                                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                                            var curr = it.get(i);
                                            ps.setDouble(1, curr.getSecond());
                                            ps.setObject(2, curr.getFirst());
                                        }

                                        @Override
                                        public int getBatchSize() {
                                            return it.size();
                                        }
                                    }
                            );
                        }
                ).then()
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    protected Map<Integer, String> errors() {
        return FIELDS;
    }

    @Override
    public OperationType myType() {
        return OperationType.ODPU_DATA;
    }
}
