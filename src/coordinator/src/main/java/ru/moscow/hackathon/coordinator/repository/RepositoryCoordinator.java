package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RepositoryCoordinator {

    Map<OperationType, CoordinatedRepository> repositories;
    Long batchSize;

    public RepositoryCoordinator(
            Map<OperationType, CoordinatedRepository> repositories,
            @Value("${main.saving.batch-size:500}") Long batchSize
    ) {
        this.repositories = repositories;
        this.batchSize = batchSize;
    }

    public void coordinate(OperationType type, List<String[]> rows) {
        Mono.just(rows)
                .doOnNext(it -> {
                    var repository = repositories.get(type);
                    for (long i = 0; i < rows.size() / batchSize + 1; i++) {
                        repository.handle(
                                type,
                                rows.stream()
                                .skip(i * batchSize)
                                .limit(batchSize)
                                .toList());
                    }
                }).subscribe();
    }
}
