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
import ru.moscow.hackathon.coordinator.repository.EventJpaRepository;
import ru.moscow.hackathon.coordinator.repository.FullBuildingInfoRepository;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FrontendService {

    ConfirmedEventsProcessor processor;
    FullBuildingInfoRepository infoRepository;
    EventJpaRepository jpaRepository;

    public Mono<Page<FENotificationDTO>> response(
            Pageable pageable
    ) {
        return Mono.just(
                jpaRepository.findAllBy(pageable)
        ).map(
                it -> it.map(v ->
                        FENotificationDTO.builder()
                                .event(v)
                                .building(
                                        processor.appraise(
                                                infoRepository.info(
                                                        v.getUnom()
                                                )
                                        )
                                ).build()
                )
        );
    }
}
