package ru.moscow.hackathon.coordinator.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.CsvInfo;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.service.WeatherService;

@RestController
@Slf4j
@RequestMapping("/weather")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class WeatherController {

    WeatherService service;

    @PostMapping("/upload")
    public Mono<StatusDTO> weather(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("csv-info") CsvInfo csvInfo
    ) {
        if (!file.getOriginalFilename().endsWith(".csv")) {
            return Mono.just(
                    new StatusDTO(
                            HttpStatus.BAD_REQUEST,
                            "Принимаются только .csv.",
                            null
                    )
            );
        } else {
            return service.process(csvInfo, file);
        }
    }
}
