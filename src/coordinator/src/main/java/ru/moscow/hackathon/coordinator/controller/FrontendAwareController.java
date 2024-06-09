package ru.moscow.hackathon.coordinator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.CurrentSituationDTO;
import ru.moscow.hackathon.coordinator.service.ConstantUpdateService;

@RestController
@Slf4j
@RequestMapping("/live-update")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FrontendAwareController {

    ConstantUpdateService service;

    @GetMapping("/gather")
    public Mono<CurrentSituationDTO> gatherCurrentSituation() {
        return service.ask();
    }
}
