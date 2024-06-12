package ru.moscow.hackathon.coordinator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.moscow.hackathon.coordinator.enums.SourceType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    String name;
    SourceType type;
    LocalDateTime registrationDatetime, resolvedDatetime;
    String region;
    String unom; //УНОМ
    String address;
    LocalDateTime eventEndedDatetime;
}
