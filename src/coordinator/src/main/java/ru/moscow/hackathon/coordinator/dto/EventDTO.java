package ru.moscow.hackathon.coordinator.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.moscow.hackathon.coordinator.enums.SourceType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class EventDTO {
    @Id
    UUID id;
    @Column(name = "description")
    String name;
    @Column(name = "system")
    @Enumerated(EnumType.STRING)
    SourceType type;
    @Column(name = "external_created")
    String registrationDatetime;
    @Column(name = "completed")
    String resolvedDatetime;
    @Column(name = "region_name")
    String region;
    String unom; //УНОМ
    String address;
    @Column(name = "external_completed")
    String eventEndedDatetime;
}
