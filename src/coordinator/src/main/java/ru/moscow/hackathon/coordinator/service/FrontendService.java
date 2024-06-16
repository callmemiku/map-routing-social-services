package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.FENotificationDTO;
import ru.moscow.hackathon.coordinator.repository.FullBuildingInfoRepository;
import ru.moscow.hackathon.coordinator.repository.jpa.EventJpaRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FrontendService {

    ConfirmedEventsProcessor processor;
    FullBuildingInfoRepository infoRepository;
    EventJpaRepository jpaRepository;

    List<String> TYPES = List.of(
            "P1 <= 0",
            "P2 <= 0",
            "T1 < min",
            "T1 > max",
            "Аварийная протечка труб в подъезде",
            "Крупные пожары",
            "Отсутствие отопления в доме",
            "Протечка труб в подъезде",
            "Сильная течь в системе отопления",
            "Температура в квартире ниже нормативной",
            "Температура в помещении общего пользования ниже нормативной",
            "Течь в системе отопления"
    );

    public Mono<Page<FENotificationDTO>> response(
            Pageable pageable
    ) {
        return Mono.just(
                jpaRepository.findAllByNameIn(TYPES, pageable)
        ).map(
                it -> it.map(v -> {

                            var address = v.getAddress();
                            var eventResolved = noTimestamp(v.getResolvedDatetime());
                            v.setRegistrationDatetime(
                                    noTimestamp(v.getRegistrationDatetime())
                            );
                            v.setEventEndedDatetime(
                                    noTimestamp(v.getEventEndedDatetime())
                            );

                            StringBuilder info = new StringBuilder();
                            var building = processor.appraise(
                                    infoRepository.info(
                                            v.getUnom()
                                    ),
                                    v
                            );
                            if (building == null) {
                                add(info, "Информация о здании", "отсутствует");
                            } else {
                                add(info, "Адрес (БТИ)", building.getAddress());
                                add(info, "Полный адрес (БТИ)", building.getAddressFull());
                                add(info, "Потребитель", building.getConsumer());
                                add(info, "Энергоэффективность", building.getEfficiency());
                                add(info, "Количество работников", building.getEmployeeCount());
                                add(info, "Отапливаемая площадь", building.getFullHeatedSquare());
                                add(info, "Этажность", building.getFloors());
                                add(info, "Время работы", building.getWorkingHours());
                                add(info, "ОДС", building.getOdsIdentity());
                                add(info, "Адрес ОДС", building.getOdsAddress());
                                add(info, "ТП", building.getWarmPointId());
                                add(info, "Адрес ТП", building.getTpAddress());
                                add(info, "Тип ТП", building.getTpType());
                                add(info, "Источник тепла", building.getTpHeatSource());
                                add(info, "Время до полного остывания", building.getCoolingSpeedFull() + " ч");

                            }
                            add(info, "Адрес", address);
                            add(info, "Дата решения", eventResolved);
                            return FENotificationDTO.builder()
                                    .event(v)
                                    .building(building)
                                    .info(info.toString())
                                    .build();
                        }
                )
        );
    }

    private void add(StringBuilder builder, String destination, Object value) {

        if (value == null) {

        } else {
            var string = value.toString();

            if (string.isEmpty() || string.isBlank()) {

            } else {
                builder.append(
                        String.format(
                                "\t%s: %s.\n",
                                destination,
                                value
                        )
                );
            }
        }
    }

    private String noTimestamp(String date) {
        return Optional.ofNullable(date)
                .map(d -> {
                            if (d.contains(".")) {
                                return d.split("\\.")[0];
                            } else return d;
                        }
                ).orElse(date);
    }
}
