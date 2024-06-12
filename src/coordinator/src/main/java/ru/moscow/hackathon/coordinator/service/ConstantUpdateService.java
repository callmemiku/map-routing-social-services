package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.CurrentSituationDTO;
import ru.moscow.hackathon.coordinator.dto.EventDTO;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ConstantUpdateService {

    WebClient webClient;

    public Mono<CurrentSituationDTO> ask() {
        return webClient.post()
                .uri("")
                .bodyValue(new EventDTO())
                .accept()
                .retrieve()
                .bodyToMono(CurrentSituationDTO.class)
                .doOnError(
                        Mono::error
                );
    }
}
