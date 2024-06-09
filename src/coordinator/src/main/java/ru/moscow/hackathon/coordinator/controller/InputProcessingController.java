package ru.moscow.hackathon.coordinator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.service.InputProcessor;

@RestController
@Slf4j
@RequestMapping("/input")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InputProcessingController {

    InputProcessor processor;

    @PostMapping(
            path = "/file"
    )
    public Mono<StatusDTO> file(
            @RequestPart("file") MultipartFile file,
            @RequestParam("type") OperationType type,
            @RequestParam("ignore-lines") Integer ignoreLines
            ) {
        return processor.processFile(
                file,
                type,
                ignoreLines
        );
    }
}
