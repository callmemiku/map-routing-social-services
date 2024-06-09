package ru.moscow.hackathon.coordinator.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public enum CustomSchedulers {

    DB_BLOCKING(
            Schedulers.newParallel("DB_BLOCKING")
    );

    Scheduler scheduler;
}
