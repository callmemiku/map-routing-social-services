package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "bti_data")
public class BtiEntity {

    @Id
    UUID id;

    String street;
    String addressNumberType;
    String addressNumber;
    String unom;
    String exteriorWallMaterial;
    String buildingClass;
    Double fullSquare;
}
