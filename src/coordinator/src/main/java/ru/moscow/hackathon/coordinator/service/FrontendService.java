package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.FENotificationDTO;
import ru.moscow.hackathon.coordinator.repository.EventRelationRepository;
import ru.moscow.hackathon.coordinator.repository.EventRepository;
import ru.moscow.hackathon.coordinator.repository.FullBuildingInfoRepository;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FrontendService {

    EventRepository eventRepository;
    EventRelationRepository relationRepository;
    FullBuildingInfoRepository infoRepository;

    public Mono<List<FENotificationDTO>> response() {
        return Flux.fromIterable(
                eventRepository.findEvents()
        ).doOnNext(
                it -> it.setUnoms(
                        relationRepository.findRelated(it.getId())
                )
        ).map(
                it -> FENotificationDTO.builder()
                        .event(it)
                        .buildings(
                                infoRepository.info(
                                        it.getUnoms()
                                )
                        ).build()
        ).collectList();
    }
}
