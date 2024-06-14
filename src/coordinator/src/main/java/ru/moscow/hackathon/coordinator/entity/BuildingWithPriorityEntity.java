package ru.moscow.hackathon.coordinator.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingWithPriorityEntity extends BuildingEntity {

    Double weightedEfficiency;
    Double coolingSpeed;

    public BuildingWithPriorityEntity(BuildingEntity parent, Double weightedEfficiency, Double coolingSpeed) {
        super(
                parent.id,
                parent.unom,
                parent.centerCoordinates,
                parent.type,
                parent.workingHours,
                parent.efficiency,
                parent.odsIdentity,
                parent.address
        );
        this.weightedEfficiency = weightedEfficiency;
        this.coolingSpeed = coolingSpeed;
    }

    public BuildingWithPriorityEntity(BuildingEntity parent) {
        super(
                parent.id,
                parent.unom,
                parent.centerCoordinates,
                parent.type,
                parent.workingHours,
                parent.efficiency,
                parent.odsIdentity,
                parent.address
        );
    }
}
