package ru.moscow.hackathon.coordinator.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuildingWithPriorityEntity {
    Integer priorityByConsumerGroup;
    Integer priorityByEfficiency;
    Integer priorityByWorkingHours;
    Double coolingSpeed;

    BuildingEntity entity;

    public int getSumOfPriorities() {
        return priorityByConsumerGroup + priorityByEfficiency + priorityByWorkingHours;
    }
}
