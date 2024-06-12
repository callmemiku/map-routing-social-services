package ru.moscow.hackathon.coordinator.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BuildingEntity {
    String unom; //y
    String centerCoordinates;//y
    String type;//y
    String workingHours;
    String efficiency;//y
    String odsIdentity;//y
}
