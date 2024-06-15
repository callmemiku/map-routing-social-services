package ru.moscow.hackathon.coordinator.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.moscow.hackathon.coordinator.entity.BuildingEntity;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FENotificationDTO {
    EventDTO event;
    BuildingEntity building;
    String info;
}
