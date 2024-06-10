package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.enums.AllowedFiletypes;
import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.python.PythonScriptCaller;
import ru.moscow.hackathon.coordinator.repository.RepositoryCoordinator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Random;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InputProcessor {

    PythonScriptCaller pythonScriptCaller;
    Random random = new Random();
    RepositoryCoordinator coordinator;

    public Mono<StatusDTO> processFile(
            MultipartFile file,
            OperationType type,
            Integer ignoreLines
    ) {
        var fileName = String.format("buffer-%d.csv", random.nextLong());
        var buffer = new File(fileName);

        return Mono.just(file)
                .map(multipartFile -> {
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
                                    convertToCsv(multipartFile, buffer);
                                } else {
                                    file.transferTo(buffer);
                                }
                            } catch (IOException e) {
                                throw new IllegalStateException("can't handle incoming file: " + e.getMessage());
                            }
                            return buffer;
                        }
                ).map(it -> {
                            try (var reader = new BufferedReader(new FileReader(it))) {
                                var split = reader.lines()
                                        .skip(ignoreLines == null ? 0 : ignoreLines)
                                        .map(
                                                row -> {
                                                    var strings = row.split(";");
                                                    if (strings.length != type.getRowWidth()) {
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
                                coordinator.coordinate(type, split);
                                return new StatusDTO(
                                        HttpStatus.OK,
                                        "Файл успешно сохранен!"
                                );
                            } catch (IOException e) {
                                throw new IllegalStateException("can't handle incoming file: " + e.getMessage());
                            }
                        }
                ).onErrorResume(
                        e -> Mono.just(
                                new StatusDTO(
                                        HttpStatus.BAD_GATEWAY,
                                        "Не удалось обработать сообщение: " + e.getMessage()
                                )
                        )
                ).doOnTerminate(
                        () -> {
                            var del = buffer.delete();
                            log.debug("BUFFER DELETION: {}", del);
                        }
                );
    }

    private void convertToCsv(MultipartFile file, File buffer) {
        var fileName = String.format("temp-%d.xlsx", random.nextLong());
        var temp = new File(fileName);
        try {
            file.transferTo(temp.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        pythonScriptCaller.convert(
                temp.getAbsolutePath(),
                buffer.getAbsolutePath()
        );
        log.debug("temp deletion: {}", temp.delete());
    }

    @Deprecated
    private void deprecated() {
        /*
        var wb = new XSSFWorkbook(temp);
        Row row;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            var currSheet = wb.getSheetAt(i);
            row = currSheet.getRow(0);
            if (row.getLastCellNum() == type.getRowWidth()) {
                for (int k = 0; k < currSheet.getLastRowNum(); k++) {
                    log.debug("converting - SHEET: {}, ROW: {}", i, k);
                    row = currSheet.getRow(k);
                    var cells = type.getCells();
                    for (int j = 0; j < cells.size(); j++) {
                        var cell = row.getCell(cells.get(j));
                        cell.setCellType(CellType.STRING);
                        writer.write(cell.toString());
                        if (j < cells.size() - 1) {
                            writer.write(DELIMITER);
                        } else {
                            writer.write("\n");
                        }
                    }
                }
            }
        }
        writer.flush();
        */
    }
}
