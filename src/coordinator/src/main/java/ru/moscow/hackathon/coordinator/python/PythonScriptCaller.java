package ru.moscow.hackathon.coordinator.python;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import ru.moscow.hackathon.coordinator.dto.MultisheetXLSXDTO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
@Slf4j
public class PythonScriptCaller {

    @Value("${app.run-from-jar}")
    Boolean jar;

    String SCRIPT_NAME = "converter.py";

    @SneakyThrows({IOException.class, InterruptedException.class})
    public void convert(
            String input,
            String output,
            MultisheetXLSXDTO.SheetDTO sheetDTO
    ) {
        File file;
        if (jar)
            file = new File("app/" + SCRIPT_NAME);
        else
            file = ResourceUtils.getFile("classpath:" + SCRIPT_NAME);
        Process process;
        if (sheetDTO == null)
            process = new ProcessBuilder(
                    "python3",
                    file.getAbsolutePath(),
                    "-i",
                    input,
                    "-o",
                    output
            ).redirectErrorStream(true)
                    .start();
        else
            process = new ProcessBuilder(
                    "python3",
                    file.getAbsolutePath(),
                    "-i",
                    input,
                    "-o",
                    output,
                    "-n",
                    sheetDTO.getSheetName()
            ).redirectErrorStream(true)
                    .start();
        var inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        var errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        inputReader.lines().forEach(e -> log.debug("[PYTHON INPUT] "));
        errorReader.lines().forEach(e -> log.debug("[PYTHON ERROR] "));

        int rc = process.waitFor();
        if (rc != 0) {
            throw new IllegalStateException("RC from python code is not 0");
        }
    }
}
