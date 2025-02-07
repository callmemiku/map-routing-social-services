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
import ru.moscow.hackathon.coordinator.enums.coefficients.BetaCoefficient;
import ru.moscow.hackathon.coordinator.enums.coefficients.BtiBuildingType;
import ru.moscow.hackathon.coordinator.enums.coefficients.BuildingType;
import ru.moscow.hackathon.coordinator.enums.coefficients.CoolingBelowNormalType;
import ru.moscow.hackathon.coordinator.enums.coefficients.EfficiencyType;
import ru.moscow.hackathon.coordinator.enums.coefficients.EmployeeCountType;
import ru.moscow.hackathon.coordinator.enums.coefficients.FullCoolingType;
import ru.moscow.hackathon.coordinator.enums.coefficients.Materials;
import ru.moscow.hackathon.coordinator.enums.coefficients.TemperatureByGroup;
import ru.moscow.hackathon.coordinator.enums.coefficients.WorkingHoursType;
import ru.moscow.hackathon.coordinator.repository.EventRelationRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor

public class ConfirmedEventsProcessor {

    WeatherGathererService gathererService;
    EventRelationRepository eventRelationRepository;

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

    public BuildingWithPriorityEntity appraise(BuildingEntity it, EventDTO v) {

        if (it == null) {
            return null;
        }

        var efc = 0.2 / EfficiencyType.priority(
                it.getEfficiency()
        );

        var group = BuildingType.priority(
                it.getType()
        );

        var groupc = 0.3 / group;

        var whc = 0.25 / WorkingHoursType.priority(
                group
        );

        var df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        var ld = LocalDateTime.parse(v.getRegistrationDatetime(), df).toLocalDate();
        var weather = gathererService.weatherOnDate(ld);

        double beta;
        try {

            var colRef = it.getMaterial();
            var bitRef = it.getMaterialBTI();
            var material = Materials.byColRef(colRef);
            if (material == null) {
                material = Materials.byMaterialBti(bitRef);
            }

            if (material == null) {
                throw new IllegalStateException("Нет достаточной информации по зданию, считаем упрощенно");
            }

            var floors = it.getFloors();
            var square = it.getFullHeatedSquare();

            if (floors == null || floors.equals(0) || square == null || square.equals(0.)) {
                throw new IllegalStateException("Нет достаточной информации по зданию, считаем упрощенно");
            }

            var volume = square * floors;
            var border = Math.cbrt(volume);
            var squareOuterFull = border * border * 6;

            beta = material.getHeatCapacity() * material.getDensity() * volume / (15 * squareOuterFull);

        } catch (Exception e) {
            beta = BetaCoefficient.priority(
                    group,
                    it.getMaterial()
            );
        }

        var normal = BtiBuildingType.byType(it.getBtiClass());

        if (normal == null) {
            normal = TemperatureByGroup.byGroup(group);
        }

        var coolingBelowNormal = computeBelowNormalCoolingTime(
                normal,
                beta,
                weather
        );

        var cbnc = 0.15 / CoolingBelowNormalType.priority(
                coolingBelowNormal
        );

        var fullCooling = computeFullCoolingTime(
                normal,
                beta,
                weather
        );

        var cfc = 0.1 / FullCoolingType.priority(
                fullCooling
        );

        var eac = 0.1 / EmployeeCountType.priority(it.getEmployeeCount());

        var k = efc + groupc + whc + cbnc + cfc + eac;
        return new BuildingWithPriorityEntity(
                it,
                k,
                fullCooling,
                coolingBelowNormal
        );
    }

    private Double computeBelowNormalCoolingTime(
            Integer normal,
            Double beta,
            Double weather
    ) {
        if (weather > normal) {
            return Double.MAX_VALUE;
        }
        double bn = normal - 1;
        if (bn == weather) {
            bn = bn + .1d;
        }
        return beta * Math.log(
                (normal - weather) / (Math.abs(bn - weather))
        );
    }

    private Double computeFullCoolingTime(
            Integer normal,
            Double beta,
            Double weather
    ) {

        if (weather > normal) {
            return Double.MAX_VALUE;
        }

        var t1 = weather > normal ? weather : normal;
        return beta * Math.log(
                (t1 - weather) / (Math.abs(weather + 1 - weather))
        );
    }
}
