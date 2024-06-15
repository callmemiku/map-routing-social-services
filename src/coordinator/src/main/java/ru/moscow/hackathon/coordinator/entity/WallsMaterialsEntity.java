package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "walls_materials_dictionary")
public class WallsMaterialsEntity {

    @Id
    UUID id;
    String typicalSeries;
    String exteriorWallMaterial;
    Double thickness;
    Double outerLayer;
    Double insulationLayer;
    Double innerLayer;
    Double calcHeatTransferResistance;
}
