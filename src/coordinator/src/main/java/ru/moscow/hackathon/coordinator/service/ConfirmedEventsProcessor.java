package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.EventDTO;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.entity.BuildingEntity;
import ru.moscow.hackathon.coordinator.entity.BuildingWithPriorityEntity;
import ru.moscow.hackathon.coordinator.enums.BuildingType;
import ru.moscow.hackathon.coordinator.enums.EfficiencyType;
import ru.moscow.hackathon.coordinator.enums.WorkingHoursType;
import ru.moscow.hackathon.coordinator.repository.EventRelationRepository;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ConfirmedEventsProcessor {

    EventRelationRepository eventRelationRepository;

    Double WEIGHT = 0.33d;

    public Mono<StatusDTO> process(List<EventDTO> events) {

        return Mono.just(events)
                .doOnNext(
                        eventRelationRepository::saveEvents
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

    public BuildingWithPriorityEntity appraise(BuildingEntity it) {
        var entity = new BuildingWithPriorityEntity(it);
        var efc = EfficiencyType.priority(
                it.getEfficiency()
        );
        var groupc = BuildingType.priority(
                it.getType()
        );
        var whc = WorkingHoursType.priority(
                "КРУГЛОСУТОЧНО" //выяснить, где!
        );
        entity.setCoolingSpeed(
                countCoolingSpeed(it)
        );
        var wep = WEIGHT / efc + WEIGHT / groupc + WEIGHT / whc;
        entity.setWeightedEfficiency(wep);
        return entity;
    }

    private Double countCoolingSpeed(BuildingEntity entity) {

        //β=c·ρ·V/(α·F)

        //z=(LN (ABS (tн-t1)) -LN (ABS (tн-t2)))·c·ρ·V/(α·F)

        return 0.0d;
    }
}
