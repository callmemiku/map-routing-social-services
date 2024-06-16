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

    String weightedEfficiency;
    String coolingSpeedFull;
    String coolingSpeedBelowNormal;

    public BuildingWithPriorityEntity(
            BuildingEntity parent,
            Double weightedEfficiency,
            Double coolingSpeedFull,
            Double coolingSpeedBelowNormal
    ) {
        this(parent);
        this.weightedEfficiency = String.format("%.2f", weightedEfficiency);
        this.coolingSpeedFull = String.format("%.2f", coolingSpeedFull);
        this.coolingSpeedBelowNormal = String.format("%.2f", coolingSpeedBelowNormal);
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
                parent.address,
                parent.odsAddress,
                parent.warmPointId,
                parent.addressFull,
                parent.consumer,
                parent.tpAddress,
                parent.tpType,
                parent.tpHeatSource,
                parent.employeeCount,
                parent.material,
                parent.materialBTI,
                parent.floors,
                parent.fullHeatedSquare,
                parent.simpleAddress
        );
    }
}
