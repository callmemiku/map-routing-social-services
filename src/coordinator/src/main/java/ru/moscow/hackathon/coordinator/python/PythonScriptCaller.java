package ru.moscow.hackathon.coordinator.python;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

@Component
public class PythonScriptCaller {

    @Value("${app.run-from-jar}")
    Boolean jar;

    String SCRIPT_NAME = "converter.py";

    @SneakyThrows({IOException.class, InterruptedException.class})
    public void convert(
            String input,
            String output
    ) {
        File file;
        if (jar)
            file = new File("app/" + SCRIPT_NAME);
        else
            file = ResourceUtils.getFile("classpath:" + SCRIPT_NAME);
        var process = new ProcessBuilder(
                "python3",
                file.getAbsolutePath(),
                "-i",
                input,
                "-o",
                output
        ).redirectErrorStream(true)
                .start();
        int rc = process.waitFor();
        if (rc != 0) {
            throw new IllegalStateException("RC from python code is not 0");
        }
    }
}
