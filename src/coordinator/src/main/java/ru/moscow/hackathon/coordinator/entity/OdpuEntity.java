package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "odpu_data")
public class OdpuEntity {
    @Id
    UUID id;
    String consumer;
    String buildingGroup;
    String unom;
    String address;
    String heatCounterNumber;
    String measurementDate;
    Double heatingVolumeIn;
    Double heatingVolumeOut;
    Double heatLeakage;
    Double supplyWaterTemp;
    Double returnWaterTemp;
    Double heatCounterHours;
    Double energyConsumption;
    String heatCounterError;
    Double temperature;
}
