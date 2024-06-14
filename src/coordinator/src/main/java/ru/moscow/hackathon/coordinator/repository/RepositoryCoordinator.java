package ru.moscow.hackathon.coordinator.repository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.enums.OperationType;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class RepositoryCoordinator {

    Map<OperationType, CoordinatedRepository> repositories;

    public Mono<Void> coordinate(OperationType type, List<String[]> rows) {
        var repository = repositories.get(type);
        return repository.handle(type, rows).flatMap(repository::doAfter);
    }
}
