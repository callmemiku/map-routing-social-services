package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.ConfirmedEventDTO;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.entity.BuildingEntity;
import ru.moscow.hackathon.coordinator.entity.BuildingWithPriorityEntity;
import ru.moscow.hackathon.coordinator.enums.BuildingType;
import ru.moscow.hackathon.coordinator.enums.EfficiencyType;
import ru.moscow.hackathon.coordinator.enums.WorkingHoursType;
import ru.moscow.hackathon.coordinator.repository.EventRelationRepository;
import ru.moscow.hackathon.coordinator.repository.FullBuildingInfoRepository;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ConfirmedEventsProcessor {

    FullBuildingInfoRepository repository;
    EventRelationRepository eventRelationRepository;

    public Mono<StatusDTO> process(List<ConfirmedEventDTO> events) {

        Map<ConfirmedEventDTO, List<BuildingEntity>> grouped = new HashMap<>();

        return Mono.just(grouped)
                .doOnNext(
                        it ->
                                events.stream()
                                        .peek(a -> a.setId(UUID.randomUUID()))
                                        .forEach(
                                                event -> it.put(
                                                        event,
                                                        repository.info(event.getUnoms())
                                                )
                                        )
                ).doOnNext(
                        map -> map.entrySet().forEach(
                                entry -> {
                                    var mapped = entry.getValue()
                                            .stream()
                                            .map(it -> {
                                                        var entity = new BuildingWithPriorityEntity();
                                                        entity.setEntity(it);
                                                        entity.setPriorityByEfficiency(
                                                                EfficiencyType.priority(
                                                                        it.getEfficiency()
                                                                )
                                                        );
                                                        entity.setPriorityByConsumerGroup(
                                                                BuildingType.priority(
                                                                        it.getType()
                                                                )
                                                        );
                                                        entity.setPriorityByWorkingHours(
                                                                WorkingHoursType.priority(
                                                                        "КРУГЛОСУТОЧНО" //выяснить, где!
                                                                )
                                                        );
                                                        entity.setCoolingSpeed(
                                                                countCoolingSpeed(it)
                                                        );
                                                        return entity;
                                                    }
                                            ).toList();
                                    entry.setValue(
                                            order(mapped)
                                    );
                                }
                        )
                ).doOnNext(
                        eventRelationRepository::save
                ).flatMap(
                        it -> Mono.just(
                                new StatusDTO(
                                        HttpStatus.OK,
                                        "saved",
                                        null
                                )
                        )
                ).onErrorResume(
                        it -> Mono.just(
                                new StatusDTO(
                                        HttpStatus.BAD_GATEWAY,
                                        "not saved: " + it.getMessage(),
                                        null
                                )
                        )
                );
    }

    private Double countCoolingSpeed(BuildingEntity entity) {

        //β=c·ρ·V/(α·F)

        //z=(LN (ABS (tн-t1)) -LN (ABS (tн-t2)))·c·ρ·V/(α·F)

        return 0.0d;
    }

    private List<BuildingEntity> order(List<BuildingWithPriorityEntity> buildingEntities) {
        return buildingEntities.stream()
                .sorted(
                        Comparator.comparing(
                                BuildingWithPriorityEntity::getSumOfPriorities
                        ).thenComparing
                                (
                                        BuildingWithPriorityEntity::getCoolingSpeed,
                                        Comparator.reverseOrder()
                                )
                ).map(BuildingWithPriorityEntity::getEntity)
                .toList();
    }
}
