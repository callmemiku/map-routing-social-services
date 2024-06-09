package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.moscow.hackathon.coordinator.dto.StatusDTO;
import ru.moscow.hackathon.coordinator.enums.AllowedFiletypes;
import ru.moscow.hackathon.coordinator.enums.OperationType;
import ru.moscow.hackathon.coordinator.repository.RepositoryCoordinator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Random;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class InputProcessor {

    Random random = new Random();
    RepositoryCoordinator coordinator;
    String DELIMITER = ";";

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

                            if (file.getOriginalFilename().endsWith(AllowedFiletypes.XLSX.value())) {
                                convertToCsv(multipartFile, fileName, type);
                            } else {
                                try {
                                    file.transferTo(buffer);
                                } catch (IOException e) {
                                    throw new IllegalStateException("can't handle incoming file.");
                                }
                            }
                            return buffer;
                        }
                ).map(it -> {
                            try(var reader = new BufferedReader(new FileReader(it))) {
                                var split = reader.lines()
                                        .skip(ignoreLines == null ? 0 : ignoreLines)
                                        .map(
                                                row -> row.split(DELIMITER)
                                        ).map(row -> {
                                            if (row.length != type.getRowWidth()) {
                                                var coolArray = new String[type.getRowWidth()];
                                                System.arraycopy(row, 0, coolArray, 0, row.length);
                                                return coolArray;
                                            } else return row;
                                        })
                                        .toList();
                                coordinator.coordinate(type, split);
                                return new StatusDTO(
                                        HttpStatus.OK,
                                        "Файл успешно сохранен!"
                                );
                            } catch (IOException e) {
                                throw new IllegalStateException("can't handle incoming file.");
                            }
                        }
                ).onErrorResume(
                        e -> Mono.just(
                                new StatusDTO(
                                        HttpStatus.BAD_GATEWAY,
                                        "Не удалось обработать сообщение: " + e.getMessage()
                                )
                        )
                ).doOnTerminate(() -> {
                    var del = buffer.delete();
                    log.debug("BUFFER DELETION: {}", del);
                });
    }

    @SneakyThrows(IOException.class)
    private void convertToCsv(MultipartFile file, String fileName, OperationType type) {
        try (var writer = new FileWriter(fileName)) {
            var wb = WorkbookFactory.create(file.getInputStream());
            Row row;
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                var currSheet = wb.getSheetAt(i);
                row = currSheet.getRow(0);
                if (row.getLastCellNum() == type.getRowWidth()) {
                    for (int k = 0; k < currSheet.getLastRowNum(); k++) {
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
        }
    }
}
