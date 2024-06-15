package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "moek_connection_data")
public class MoekConnectionEntity {
    @Id
    UUID id;
    String heatingStationNumber;
    String heatingStationType;
    String heatSource;
    String cityDistrict;
    String address;
    Double thermalLoadHws;
    Double thermalLoadBuilding;
}
