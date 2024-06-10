package ru.moscow.hackathon.coordinator.python;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class PythonScriptCaller {

    @SneakyThrows({IOException.class, InterruptedException.class})
    public void convert(
            String input,
            String output
    ) {
        File file = ResourceUtils.getFile("classpath:converter.py");
        var process = new ProcessBuilder(
                "python3",
                file.getAbsolutePath(),
                "-i",
                input,
                "-o",
                output
        ).redirectErrorStream(true)
                .start();
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        var error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        reader.lines().forEach(System.out::println);
        error.lines().forEach(System.out::println);
        int rc = process.waitFor();
        if (rc != 0) {
            throw new IllegalStateException("RC from python code is not 0");
        }
    }
}
