package ru.moscow.hackathon.coordinator.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingWithPriorityEntity extends BuildingEntity {
    Double weightedEfficiency;
    Double coolingSpeed;
}
