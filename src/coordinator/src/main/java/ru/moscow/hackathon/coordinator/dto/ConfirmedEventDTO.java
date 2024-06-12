package ru.moscow.hackathon.coordinator.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ConfirmedEventDTO extends EventDTO {

        UUID id;
        List<String> unoms;
}
