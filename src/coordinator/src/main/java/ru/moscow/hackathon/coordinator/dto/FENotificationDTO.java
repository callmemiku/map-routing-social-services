package ru.moscow.hackathon.coordinator.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.moscow.hackathon.coordinator.entity.BuildingEntity;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FENotificationDTO {
    EventDTO event;
    List<BuildingEntity> buildings;
}
