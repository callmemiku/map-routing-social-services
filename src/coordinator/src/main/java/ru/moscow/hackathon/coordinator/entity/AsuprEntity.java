package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "asupr_data")
public class AsuprEntity {
    @Id
    UUID id;
    String idUU;
    String address;
    String addressFull;
    String region;
    String unom;
    String buildingGroup;
    String odsIdentification;
    String odsAddress;
    String consumer;
    String warmPointId;
}
