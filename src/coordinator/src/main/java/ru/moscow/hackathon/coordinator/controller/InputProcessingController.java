package ru.moscow.hackathon.coordinator.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.MultisheetXLSXDTO;
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
            @RequestPart(value = "file", required = true) MultipartFile file,
            @RequestParam(value = "type", required = true) OperationType type,
            @RequestParam(value = "ignore-lines", required = false) Integer ignoreLines
    ) {

        if (ignoreLines != null && ignoreLines < 0) {
            return Mono.just(
                    new StatusDTO(
                            HttpStatus.BAD_REQUEST,
                            "Количество пропускаемых строк должно быть больше или равно нулю.",
                            null
                    )
            );
        }

        return processor.processFile(
                file,
                type,
                ignoreLines
        );
    }

    @PostMapping(
            path = "/xlsx-multiple-sheets",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Mono<StatusDTO> wideXlsx(
            @RequestPart(value = "sheet-info") MultisheetXLSXDTO dto,
            @RequestPart(value = "file", required = true) MultipartFile file
    ) {
        return processor.processFile(
                file,
                dto
        );
    }
}
