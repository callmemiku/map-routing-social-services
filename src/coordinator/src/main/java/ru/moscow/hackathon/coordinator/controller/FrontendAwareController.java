package ru.moscow.hackathon.coordinator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.FENotificationDTO;
import ru.moscow.hackathon.coordinator.service.FrontendService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/update")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class FrontendAwareController {

    FrontendService service;

    @GetMapping("/gather")
    public Mono<List<FENotificationDTO>> gather() {
        return service.response();
    }
}
