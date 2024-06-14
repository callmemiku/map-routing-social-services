package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@FieldDefaults(level = AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class BuildingEntity {
    @Id
    UUID id;
    String unom; //y
    String centerCoordinates;//y
    String type;//y
    String workingHours;
    String efficiency;//y
    String odsIdentity;//y
    String address;
}
