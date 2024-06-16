package ru.moscow.hackathon.coordinator.repository.implementations;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

import reactor.core.scheduler.Schedulers;
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
public class AddressRegistryDataRepository extends CoordinatedRepository {

    JdbcTemplate jdbcTemplate;
    Map<Integer, String> FIELDS = new HashMap<>();

    @PostConstruct
    public void init() {
        FIELDS.put(2,"full_address");
        FIELDS.put(3,"unom");
        FIELDS.put(4, "simple_address");
        FIELDS.put(5,"geodata");
        FIELDS.put(6,"geodata_center");
    }

    @Override
    public Mono<List<Pair<UUID, List<String>>>> handle(OperationType type, List<String[]> rows) {
        log.debug("[ADDRESS REGISTRY REPO] saving {} records", rows.size());
        List<Pair<UUID, List<String>>> storage = new ArrayList<>();
        return Mono.just(rows)
                .map(it ->
                        jdbcTemplate.batchUpdate(
                                """
                                    insert into address_registry_data(
                                        id,
                                        full_address,
                                        unom,
                                        simple_address,
                                        "geoData",
                                        geodata_center
                                    ) VALUES (?, ?, ?, ?, ?, ?)
                                    """,
                                new BatchPreparedStatementSetter() {
                                    @Override
                                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                                        var current = it.get(i);
                                        var id = UUID.randomUUID();
                                        ps.setObject(1, id); // id
                                        setString(id, ps, 2, current[0]); // full_address
                                        setString(id, ps, 3, current[1]); // unom
                                        setString(id, ps, 4, current[2]); // simple_address
                                        setString(id, ps, 5, current[3]); // geoData
                                        setString(id, ps, 6, current[4]); // geodata_center
                                        storage.add(Pair.of(id, List.of(current[0])));
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
                .doOnNext(it -> {
                            log.debug("[AR DATA] saving ts_vectors for {} records", it.size());
                            jdbcTemplate.batchUpdate(
                                    "insert into address_registry_data_vector(id, full_address_vector) values (?, to_tsvector(?))",
                                    new BatchPreparedStatementSetter() {
                                        @Override
                                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                                            var curr = it.get(i);
                                            ps.setObject(1, curr.getFirst());
                                            ps.setString(2, curr.getSecond());
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
        return OperationType.ADDRESS_REGISTRY_DATA;
    }
}
