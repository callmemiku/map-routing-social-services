package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "power_efficiency_data")
public class PowerEfficiencyEntity {
    @Id
    UUID id;
    String building;
    Double fullSquare;
    Double fullSquareHeated;
    Integer employees_number;
    String buildingType;
    String energyClass;
    Double buildingWear;
    String commissioningYear;
}
