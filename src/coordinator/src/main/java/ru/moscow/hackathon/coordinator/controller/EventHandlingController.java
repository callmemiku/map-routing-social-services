package ru.moscow.hackathon.coordinator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.EventDTO;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.service.ConfirmedEventsProcessor;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/event")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class EventHandlingController {

    ConfirmedEventsProcessor processor;

    @PostMapping("/post")
    public Mono<StatusDTO> acquireEvent(
            @RequestBody List<EventDTO> events
    ) {
        return processor.process(events);
    }
}
