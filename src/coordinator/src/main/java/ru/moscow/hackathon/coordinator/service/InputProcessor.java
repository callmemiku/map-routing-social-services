package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.MultisheetXLSXDTO;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.enums.AllowedFiletypes;
import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.python.PythonScriptCaller;
import ru.moscow.hackathon.coordinator.repository.RepositoryCoordinator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InputProcessor {

    PythonScriptCaller pythonScriptCaller;
    Random random = new Random();
    RepositoryCoordinator coordinator;
    BatchSplitter batchSplitter;

    public Mono<StatusDTO> processFile(
            MultipartFile file,
            OperationType type,
            Integer ignoreLines
    ) {
        return processFile(file, type, ignoreLines, null);
    }

    public Mono<StatusDTO> processFile(
            MultipartFile file,
            MultisheetXLSXDTO xlsx
    ) {
        List<StatusDTO> list = new ArrayList<>();

        xlsx.getSheets()
                .forEach(
                        it -> processFile(file, it.getType(), 2, it).subscribe(list::add)
                );

        return Mono.just(
                list
        ).map(
                lst -> {
                    var bad = lst.stream()
                            .filter(
                                    it -> !it.status().is2xxSuccessful()
                            ).toList();
                    if (!bad.isEmpty()) {

                        var errors = bad.stream().map(
                                it -> String.format("{%s, %s}\n", it.sheetName(), it.message())
                        ).collect(Collectors.joining());

                        return new StatusDTO(
                                HttpStatus.UNPROCESSABLE_ENTITY,
                                String.format("Следующие страницы не сохранены: %s", errors),
                                bad.stream().map(StatusDTO::sheetName).collect(Collectors.joining(","))
                        );
                    } else {
                        return new StatusDTO(
                                HttpStatus.OK,
                                "Все страницы успешно сохранены!",
                                "all"
                        );

                    }
                }
        );
    }

    private Mono<StatusDTO> processFile(
            MultipartFile file,
            OperationType type,
            Integer ignoreLines,
            MultisheetXLSXDTO.SheetDTO sheet
    ) {
        var fileName = String.format("buffer-%d.csv", random.nextLong());
        var buffer = new File(fileName);

        return Mono.just(file)
                .doOnNext(multipartFile -> {
                            if (
                                    multipartFile.getOriginalFilename() == null ||
                                            EnumSet.allOf(AllowedFiletypes.class)
                                                    .stream()
                                                    .map(AllowedFiletypes::value)
                                                    .noneMatch(it -> multipartFile.getOriginalFilename().endsWith(it))
                            ) {
                                throw new IllegalArgumentException("unsupported file type. allowed: xlsx, csv");
                            }
                            try {
                                var created = buffer.createNewFile();
                                if (created) log.debug("File created");
                                if (file.getOriginalFilename().endsWith(AllowedFiletypes.XLSX.value())) {
                                    log.debug("Converting .xlsx to .csv.");
                                    convertToCsv(multipartFile, buffer, sheet);
                                    log.debug("Converted .xlsx to .csv.");
                                } else {
                                    file.transferTo(buffer.toPath());
                                }
                            } catch (IOException e) {
                                throw new IllegalStateException(e.getMessage());
                            }
                        }
                ).map(it -> {
                            try (var reader = new BufferedReader(new FileReader(buffer))) {

                                if (ignoreLines != null && ignoreLines > 0) {
                                    if (reader.readLine().split(";").length != type.getRowWidth())
                                        throw new IllegalStateException("Количество колонок в хедере не совпадает с ожидаемым.");
                                }

                                return reader.lines()
                                        .skip(ignoreLines == null ? 0 : ignoreLines)
                                        .map(
                                                row -> {
                                                    var strings = row.split(";");
                                                    if (strings.length < type.getRowWidth()) {
                                                        var coolArray = new String[type.getRowWidth()];
                                                        System.arraycopy(strings, 0, coolArray, 0, strings.length);
                                                        return coolArray;
                                                    } else return strings;
                                                }
                                        ).map(
                                                row -> {
                                                    var cells = type.getCells();
                                                    var resultArr = new String[cells.size()];
                                                    for (int i = 0; i < resultArr.length; i++) {
                                                        resultArr[i] = row[cells.get(i)];
                                                    }
                                                    return resultArr;
                                                }
                                        ).toList();
                            } catch (IOException e) {
                                throw new IllegalStateException(e.getMessage());
                            }
                        }
                ).flatMapMany(
                        split -> Flux.fromIterable(batchSplitter.toBatches(split))
                ).parallel()
                .map(v -> coordinator.coordinate(type, v))
                .doOnNext(Mono::subscribe)
                .sequential()
                .collectList()
                .map(any -> new StatusDTO(
                                HttpStatus.OK,
                                sheet == null ? "Файл успешно сохранен!" : "Страница успешно сохранена!",
                                sheet == null ? null : sheet.getSheetName()
                        )
                ).onErrorResume(
                        e -> Mono.just(
                                new StatusDTO(
                                        HttpStatus.BAD_GATEWAY,
                                        "Не удалось обработать сообщение: " + e.getMessage(),
                                        sheet == null ? null : sheet.getSheetName()
                                )
                        )
                ).doOnTerminate(
                        () -> {
                            var del = buffer.delete();
                            log.debug("File buffer deletion status: {}", del);
                        }
                );
    }

    private void convertToCsv(MultipartFile file, File buffer, MultisheetXLSXDTO.SheetDTO sheet) {
        var fileName = String.format("temp-%d.xlsx", random.nextLong());
        var temp = new File(fileName);
        try {
            file.transferTo(temp.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        pythonScriptCaller.convert(
                temp.getAbsolutePath(),
                buffer.getAbsolutePath(),
                sheet
        );
        log.debug("temp deletion: {}", temp.delete());
    }
}
