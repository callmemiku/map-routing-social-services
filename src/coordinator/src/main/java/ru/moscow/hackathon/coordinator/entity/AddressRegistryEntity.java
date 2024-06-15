package ru.moscow.hackathon.coordinator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "address_registry_data")
public class AddressRegistryEntity {
    @Id
    UUID id;
    String geoData;
    String geodataCenter;
    String unom;
}
