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

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventHandlerService {


    public Mono<StatusDTO> handleEvent(EventDTO event) {


        return Mono.just(
                        new StatusDTO(
                                HttpStatus.OK,
                                "Событие принято в обработку.",
                                null
                        )
                );
    }
}
