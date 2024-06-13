package ru.moscow.hackathon.coordinator.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BatchSplitter {
    Long batchSize;

    public BatchSplitter(
            @Value("${main.saving.batch-size:500}") Long batchSize
    ) {
        this.batchSize = batchSize;
    }

    public <T> List<List<T>> toBatches(List<T> rows) {
        List<List<T>> lists = new ArrayList<>();
        for (long i = 0; i < rows.size() / batchSize + 1; i++) {
            lists.add(
                    rows.stream()
                            .skip(i * batchSize)
                            .limit(batchSize)
                            .toList());
        }
        return lists;
    }

    public <T> List<List<T>> toBatches(List<T> rows, Long bs) {
        List<List<T>> lists = new ArrayList<>();
        for (long i = 0; i < rows.size() / bs + 1; i++) {
            lists.add(
                    rows.stream()
                            .skip(i * bs)
                            .limit(bs)
                            .toList());
        }
        return lists;
    }
}
